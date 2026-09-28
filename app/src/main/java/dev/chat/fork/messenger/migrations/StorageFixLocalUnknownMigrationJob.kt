package dev.chat.fork.messenger.migrations

import org.signal.core.util.logging.Log
import org.signal.core.util.withinTransaction
import dev.chat.fork.messenger.database.SignalDatabase
import dev.chat.fork.messenger.dependencies.AppDependencies
import dev.chat.fork.messenger.jobmanager.Job
import dev.chat.fork.messenger.jobs.MultiDeviceStorageSyncRequestJob
import dev.chat.fork.messenger.jobs.StorageSyncJob
import dev.chat.fork.messenger.keyvalue.SignalStore

/**
 * Remove local unknown storage ids not in local storage service manifest.
 */
internal class StorageFixLocalUnknownMigrationJob(
  parameters: Parameters = Parameters.Builder().build()
) : MigrationJob(parameters) {

  companion object {
    val TAG = Log.tag(StorageFixLocalUnknownMigrationJob::class.java)
    const val KEY = "StorageFixLocalUnknownMigrationJob"
  }

  override fun getFactoryKey(): String = KEY

  override fun isUiBlocking(): Boolean = false

  @Suppress("UsePropertyAccessSyntax")
  override fun performMigration() {
    val localStorageIds = SignalStore.storageService.manifest.storageIds.toSet()
    val unknownLocalIds = SignalDatabase.unknownStorageIds.getAllUnknownIds().toSet()
    val danglingLocalUnknownIds = unknownLocalIds - localStorageIds

    if (danglingLocalUnknownIds.isEmpty()) {
      return
    }

    Log.w(TAG, "Removing ${danglingLocalUnknownIds.size} dangling unknown ids")

    SignalDatabase.rawDatabase.withinTransaction {
      SignalDatabase.unknownStorageIds.delete(danglingLocalUnknownIds)
    }

    val jobManager = AppDependencies.jobManager

    if (SignalStore.account.isMultiDevice) {
      Log.i(TAG, "Multi-device.")
      jobManager.startChain(StorageSyncJob.forLocalChange())
        .then(MultiDeviceStorageSyncRequestJob())
        .enqueue()
    } else {
      Log.i(TAG, "Single-device.")
      jobManager.add(StorageSyncJob.forRemoteChange())
    }
  }

  override fun shouldRetry(e: Exception): Boolean = false

  class Factory : Job.Factory<StorageFixLocalUnknownMigrationJob> {
    override fun create(parameters: Parameters, serializedData: ByteArray?): StorageFixLocalUnknownMigrationJob {
      return StorageFixLocalUnknownMigrationJob(parameters)
    }
  }
}
