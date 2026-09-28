package dev.chat.fork.messenger.migrations

import org.signal.core.util.logging.Log
import dev.chat.fork.messenger.dependencies.AppDependencies
import dev.chat.fork.messenger.jobmanager.Job
import dev.chat.fork.messenger.jobs.GroupDeletedBackfillWorkerJob

/**
 * Kicks off clearing metadata of existing deleted groups (left and thread deleted) by enqueueing a [GroupDeletedBackfillWorkerJob].
 */
internal class GroupDeletedBackfillMigrationJob(
  parameters: Parameters = Parameters.Builder().build()
) : MigrationJob(parameters) {

  companion object {
    val TAG = Log.tag(GroupDeletedBackfillMigrationJob::class.java)
    const val KEY = "GroupDeletedBackfillMigrationJob"
  }

  override fun getFactoryKey(): String = KEY

  override fun isUiBlocking(): Boolean = false

  override fun performMigration() {
    AppDependencies.jobManager.add(GroupDeletedBackfillWorkerJob())
  }

  override fun shouldRetry(e: Exception): Boolean = false

  class Factory : Job.Factory<GroupDeletedBackfillMigrationJob> {
    override fun create(parameters: Parameters, serializedData: ByteArray?): GroupDeletedBackfillMigrationJob {
      return GroupDeletedBackfillMigrationJob(parameters)
    }
  }
}
