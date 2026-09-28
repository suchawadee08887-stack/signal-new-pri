package dev.chat.fork.messenger.migrations

import org.signal.core.util.logging.Log
import dev.chat.fork.messenger.dependencies.AppDependencies
import dev.chat.fork.messenger.jobmanager.Job
import dev.chat.fork.messenger.jobs.MultiDeviceKeysUpdateJob
import dev.chat.fork.messenger.jobs.StorageForcePushJob
import dev.chat.fork.messenger.jobs.Svr2MirrorJob
import dev.chat.fork.messenger.keyvalue.SignalStore

/**
 * Migration for when we introduce the Account Entropy Pool (AEP).
 */
internal class AepMigrationJob(
  parameters: Parameters = Parameters.Builder().build()
) : MigrationJob(parameters) {

  companion object {
    val TAG = Log.tag(AepMigrationJob::class.java)
    const val KEY = "AepMigrationJob"
  }

  override fun getFactoryKey(): String = KEY

  override fun isUiBlocking(): Boolean = false

  override fun performMigration() {
    if (!SignalStore.account.isRegistered) {
      Log.w(TAG, "Not registered! Skipping.")
      return
    }

    if (SignalStore.account.isLinkedDevice) {
      Log.i(TAG, "Not primary, skipping.")
      return
    }

    AppDependencies.jobManager.add(Svr2MirrorJob())
    if (SignalStore.account.isMultiDevice) {
      AppDependencies.jobManager.add(MultiDeviceKeysUpdateJob())
    }
    AppDependencies.jobManager.add(StorageForcePushJob())
  }

  override fun shouldRetry(e: Exception): Boolean = false

  class Factory : Job.Factory<AepMigrationJob> {
    override fun create(parameters: Parameters, serializedData: ByteArray?): AepMigrationJob {
      return AepMigrationJob(parameters)
    }
  }
}
