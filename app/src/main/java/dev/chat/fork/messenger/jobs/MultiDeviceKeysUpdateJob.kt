package dev.chat.fork.messenger.jobs

import org.signal.core.util.logging.Log
import org.signal.network.exceptions.PushNetworkException
import dev.chat.fork.messenger.dependencies.AppDependencies
import dev.chat.fork.messenger.jobmanager.Job
import dev.chat.fork.messenger.jobmanager.impl.NetworkConstraint
import dev.chat.fork.messenger.jobmanager.impl.SealedSenderConstraint
import dev.chat.fork.messenger.keyvalue.SignalStore
import dev.chat.fork.messenger.net.NotPushRegisteredException
import dev.chat.fork.messenger.recipients.Recipient
import org.whispersystems.signalservice.api.crypto.UntrustedIdentityException
import org.whispersystems.signalservice.api.messages.multidevice.KeysMessage
import org.whispersystems.signalservice.api.messages.multidevice.SignalServiceSyncMessage
import org.whispersystems.signalservice.api.push.exceptions.ServerRejectedException
import java.io.IOException

class MultiDeviceKeysUpdateJob private constructor(parameters: Parameters) : BaseJob(parameters) {

  companion object {
    const val KEY: String = "MultiDeviceKeysUpdateJob"

    private val TAG = Log.tag(MultiDeviceKeysUpdateJob::class.java)
  }

  constructor() : this(
    Parameters.Builder()
      .setQueue("MultiDeviceKeysUpdateJob")
      .setMaxInstancesForFactory(2)
      .addConstraint(NetworkConstraint.KEY)
      .addConstraint(SealedSenderConstraint.KEY)
      .setMaxAttempts(10)
      .build()
  )

  override fun serialize(): ByteArray? = null

  override fun getFactoryKey(): String = KEY

  @Throws(IOException::class, UntrustedIdentityException::class)
  public override fun onRun() {
    if (!Recipient.self().isRegistered) {
      throw NotPushRegisteredException()
    }

    if (!SignalStore.account.isMultiDevice) {
      Log.i(TAG, "Not multi device, aborting...")
      return
    }

    if (SignalStore.account.isLinkedDevice) {
      Log.i(TAG, "Not primary device, aborting...")
      return
    }

    val syncMessage = SignalServiceSyncMessage.forKeys(
      KeysMessage(
        storageService = SignalStore.storageService.storageKey,
        accountEntropyPool = SignalStore.account.accountEntropyPool,
        mediaRootBackupKey = SignalStore.backup.mediaRootBackupKey
      )
    )

    AppDependencies.signalServiceMessageSender.sendSyncMessage(syncMessage)
  }

  public override fun onShouldRetry(e: Exception): Boolean {
    if (e is ServerRejectedException) return false
    return e is PushNetworkException
  }

  override fun onFailure() {
  }

  class Factory : Job.Factory<MultiDeviceKeysUpdateJob?> {
    override fun create(parameters: Parameters, serializedData: ByteArray?): MultiDeviceKeysUpdateJob {
      return MultiDeviceKeysUpdateJob(parameters)
    }
  }
}
