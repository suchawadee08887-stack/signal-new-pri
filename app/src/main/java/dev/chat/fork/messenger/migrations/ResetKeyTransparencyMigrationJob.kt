package dev.chat.fork.messenger.migrations

import dev.chat.fork.messenger.database.SignalDatabase
import dev.chat.fork.messenger.jobmanager.Job
import dev.chat.fork.messenger.jobs.CheckKeyTransparencyJob
import dev.chat.fork.messenger.keyvalue.SignalStore

/**
 * Clears all existing key transparency data
 */
internal class ResetKeyTransparencyMigrationJob private constructor(parameters: Parameters) : MigrationJob(parameters) {

  companion object {
    const val KEY = "ResetKeyTransparencyMigrationJob"
  }

  internal constructor() : this(Parameters.Builder().build())

  override fun isUiBlocking(): Boolean = false

  override fun getFactoryKey(): String = KEY

  override fun performMigration() {
    SignalStore.account.distinguishedHead = null
    SignalStore.misc.nextKeyTransparencyTime = 0
    SignalDatabase.recipients.clearAllKeyTransparencyData()
    CheckKeyTransparencyJob.enqueueIfNecessary(addDelay = false)
  }

  override fun shouldRetry(e: Exception): Boolean = false

  class Factory : Job.Factory<ResetKeyTransparencyMigrationJob> {
    override fun create(parameters: Parameters, serializedData: ByteArray?): ResetKeyTransparencyMigrationJob {
      return ResetKeyTransparencyMigrationJob(parameters)
    }
  }
}
