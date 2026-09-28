package dev.chat.fork.messenger.jobs

import org.signal.core.util.logging.Log
import org.signal.network.exceptions.NonSuccessfulResponseCodeException
import dev.chat.fork.messenger.dependencies.AppDependencies
import dev.chat.fork.messenger.jobmanager.Job
import dev.chat.fork.messenger.jobmanager.impl.NetworkConstraint
import dev.chat.fork.messenger.keyvalue.SignalStore
import dev.chat.fork.messenger.pin.SvrRepository
import dev.chat.fork.messenger.util.TextSecurePreferences
import java.io.IOException
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

/**
 * Refresh KBS authentication credentials for talking to KBS during re-registration.
 */
class RefreshSvrCredentialsJob private constructor(parameters: Parameters) : BaseJob(parameters) {

  companion object {
    const val KEY = "RefreshKbsCredentialsJob"

    private val TAG = Log.tag(RefreshSvrCredentialsJob::class.java)
    private val FREQUENCY: Duration = 15.days

    @JvmStatic
    fun enqueueIfNecessary() {
      if (SignalStore.svr.hasPin() && SignalStore.account.isRegistered && !TextSecurePreferences.isUnauthorizedReceived(AppDependencies.application)) {
        val lastTimestamp = SignalStore.svr.lastRefreshAuthTimestamp
        if (lastTimestamp + FREQUENCY.inWholeMilliseconds < System.currentTimeMillis() || lastTimestamp > System.currentTimeMillis()) {
          AppDependencies.jobManager.add(RefreshSvrCredentialsJob())
        } else {
          Log.d(TAG, "Do not need to refresh credentials. Last refresh: $lastTimestamp")
        }
      }
    }
  }

  private constructor() : this(
    parameters = Parameters.Builder()
      .setQueue("RefreshKbsCredentials")
      .addConstraint(NetworkConstraint.KEY)
      .setMaxInstancesForQueue(2)
      .setMaxAttempts(3)
      .setLifespan(1.days.inWholeMilliseconds)
      .build()
  )

  override fun serialize(): ByteArray? = null

  override fun getFactoryKey(): String = KEY

  override fun onRun() {
    if (!SignalStore.account.isRegistered) {
      Log.w(TAG, "Not registered! Skipping.")
      return
    }

    if (TextSecurePreferences.isUnauthorizedReceived(context)) {
      Log.i(TAG, "No longer authorized. Ignoring.")
      return
    }

    SvrRepository.refreshAndStoreAuthorization()
  }

  override fun onShouldRetry(e: Exception): Boolean {
    return e is IOException && e !is NonSuccessfulResponseCodeException
  }

  override fun onFailure() = Unit

  class Factory : Job.Factory<RefreshSvrCredentialsJob> {
    override fun create(parameters: Parameters, serializedData: ByteArray?): RefreshSvrCredentialsJob {
      return RefreshSvrCredentialsJob(parameters)
    }
  }
}
