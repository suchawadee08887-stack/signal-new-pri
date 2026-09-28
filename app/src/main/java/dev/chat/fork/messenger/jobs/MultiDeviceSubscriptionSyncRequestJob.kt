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
import org.whispersystems.signalservice.api.messages.multidevice.SignalServiceSyncMessage
import org.whispersystems.signalservice.api.push.exceptions.ServerRejectedException

/**
 * Sends a sync message to linked devices to notify them to refresh subscription status.
 */
class MultiDeviceSubscriptionSyncRequestJob private constructor(parameters: Parameters) : BaseJob(parameters) {

  companion object {
    const val KEY = "MultiDeviceSubscriptionSyncRequestJob"

    private val TAG = Log.tag(MultiDeviceSubscriptionSyncRequestJob::class.java)

    @JvmStatic
    fun enqueue() {
      val job = MultiDeviceSubscriptionSyncRequestJob(
        Parameters.Builder()
          .setQueue("MultiDeviceSubscriptionSyncRequestJob")
          .setMaxInstancesForFactory(2)
          .addConstraint(NetworkConstraint.KEY)
          .addConstraint(SealedSenderConstraint.KEY)
          .setMaxAttempts(10)
          .build()
      )

      AppDependencies.jobManager.add(job)
    }
  }

  override fun serialize(): ByteArray? = null

  override fun getFactoryKey(): String = KEY

  override fun onFailure() {
    Log.w(TAG, "Did not succeed!")
  }

  override fun onRun() {
    if (!Recipient.self().isRegistered) {
      throw NotPushRegisteredException()
    }

    if (!SignalStore.account.isMultiDevice) {
      Log.i(TAG, "Not multi device, aborting...")
      return
    }

    val messageSender = AppDependencies.signalServiceMessageSender

    messageSender.sendSyncMessage(SignalServiceSyncMessage.forFetchLatest(SignalServiceSyncMessage.FetchType.SUBSCRIPTION_STATUS))
  }

  override fun onShouldRetry(e: Exception): Boolean {
    return e is PushNetworkException && e !is ServerRejectedException
  }

  class Factory : Job.Factory<MultiDeviceSubscriptionSyncRequestJob> {
    override fun create(parameters: Parameters, serializedData: ByteArray?): MultiDeviceSubscriptionSyncRequestJob {
      return MultiDeviceSubscriptionSyncRequestJob(parameters)
    }
  }
}
