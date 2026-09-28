package dev.chat.fork.messenger.migrations

import org.signal.core.util.logging.Log
import org.signal.core.util.logging.Log.tag
import dev.chat.fork.messenger.database.SignalDatabase
import dev.chat.fork.messenger.dependencies.AppDependencies
import dev.chat.fork.messenger.jobmanager.Job
import dev.chat.fork.messenger.jobs.RefreshAttributesJob
import dev.chat.fork.messenger.jobs.RefreshOwnProfileJob
import dev.chat.fork.messenger.keyvalue.SignalStore

/**
 * Reuploads user's attributes followed by a download of their profile and a reset of their KT data
 */
internal class KeyTransparencyUsernameMigrationJob private constructor(parameters: Parameters) : MigrationJob(parameters) {

  companion object {

    const val KEY = "KeyTransparencyUsernameMigrationJob"

    private val TAG: String = tag(KeyTransparencyUsernameMigrationJob::class.java)
  }

  internal constructor() : this(Parameters.Builder().build())

  override fun isUiBlocking(): Boolean = false

  override fun getFactoryKey(): String = KEY

  override fun performMigration() {
    Log.i(TAG, "Resetting KT data and refreshing attributes")
    SignalStore.account.distinguishedHead = null
    SignalStore.misc.nextKeyTransparencyTime = 0
    SignalDatabase.recipients.clearAllKeyTransparencyData()

    AppDependencies.jobManager.startChain(RefreshAttributesJob())
      .then(RefreshOwnProfileJob())
      .enqueue()
  }

  override fun shouldRetry(e: Exception): Boolean = false

  class Factory : Job.Factory<KeyTransparencyUsernameMigrationJob> {
    override fun create(parameters: Parameters, serializedData: ByteArray?): KeyTransparencyUsernameMigrationJob {
      return KeyTransparencyUsernameMigrationJob(parameters)
    }
  }
}
