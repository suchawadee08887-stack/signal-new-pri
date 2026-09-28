package dev.chat.fork.messenger.migrations

import org.signal.core.util.logging.Log
import dev.chat.fork.messenger.dependencies.AppDependencies
import dev.chat.fork.messenger.jobmanager.Job
import dev.chat.fork.messenger.jobs.StorageForcePushJob
import dev.chat.fork.messenger.keyvalue.SignalStore
import dev.chat.fork.messenger.recipients.Recipient

/**
 * A job that marks all contacts and groups as needing to be synced, so that we'll update the
 * storage records with the new avatar color field.
 */
internal class AvatarColorStorageServiceMigrationJob(
  parameters: Parameters = Parameters.Builder().build()
) : MigrationJob(parameters) {

  companion object {
    val TAG = Log.tag(AvatarColorStorageServiceMigrationJob::class.java)
    const val KEY = "AvatarColorStorageServiceMigrationJob"
  }

  override fun getFactoryKey(): String = KEY

  override fun isUiBlocking(): Boolean = false

  override fun performMigration() {
    if (!Recipient.isSelfSet) {
      return
    }

    if (!SignalStore.account.isRegistered) {
      return
    }

    AppDependencies.jobManager.add(StorageForcePushJob())
  }

  override fun shouldRetry(e: Exception): Boolean = false

  class Factory : Job.Factory<AvatarColorStorageServiceMigrationJob> {
    override fun create(parameters: Parameters, serializedData: ByteArray?): AvatarColorStorageServiceMigrationJob {
      return AvatarColorStorageServiceMigrationJob(parameters)
    }
  }
}
