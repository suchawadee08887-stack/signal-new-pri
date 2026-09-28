package dev.chat.fork.messenger.migrations

import dev.chat.fork.messenger.jobmanager.Job
import dev.chat.fork.messenger.jobs.BackfillNotifiedStateJob

/**
 * Kicks off a background job to clean up dead notification state left behind by V318. See [BackfillNotifiedStateJob].
 */
internal class BackfillNotifiedStateMigrationJob(
  parameters: Parameters = Parameters.Builder().build()
) : MigrationJob(parameters) {

  companion object {
    const val KEY = "BackfillNotifiedStateMigrationJob"
  }

  override fun getFactoryKey(): String = KEY

  override fun isUiBlocking(): Boolean = false

  override fun performMigration() {
    BackfillNotifiedStateJob.enqueue()
  }

  override fun shouldRetry(e: Exception): Boolean = false

  class Factory : Job.Factory<BackfillNotifiedStateMigrationJob> {
    override fun create(parameters: Parameters, serializedData: ByteArray?): BackfillNotifiedStateMigrationJob {
      return BackfillNotifiedStateMigrationJob(parameters)
    }
  }
}
