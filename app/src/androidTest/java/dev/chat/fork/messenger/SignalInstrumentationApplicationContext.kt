package dev.chat.fork.messenger

import android.content.Context
import org.signal.core.util.concurrent.SignalExecutors
import org.signal.core.util.logging.AndroidLogger
import org.signal.core.util.logging.Log
import org.signal.libsignal.protocol.logging.SignalProtocolLoggerProvider
import dev.chat.fork.messenger.database.LogDatabase
import dev.chat.fork.messenger.dependencies.AppDependencies
import dev.chat.fork.messenger.dependencies.ApplicationDependencyProvider
import dev.chat.fork.messenger.dependencies.InstrumentationApplicationDependencyProvider
import dev.chat.fork.messenger.keyvalue.SignalStore
import dev.chat.fork.messenger.logging.CustomSignalProtocolLogger
import dev.chat.fork.messenger.logging.PersistentLogger
import dev.chat.fork.messenger.testing.InMemoryLogger
import dev.chat.fork.messenger.testing.TestRemoteConfig
import dev.chat.fork.messenger.util.Environment

/**
 * Application context for running instrumentation tests (aka androidTests).
 */
class SignalInstrumentationApplicationContext : ApplicationContext() {

  val inMemoryLogger: InMemoryLogger = InMemoryLogger()

  override fun attachBaseContext(base: Context?) {
    Environment.IS_INSTRUMENTATION = true
    super.attachBaseContext(base)
  }

  override fun initializeAppDependencies() {
    val default = ApplicationDependencyProvider(this)
    AppDependencies.init(this, InstrumentationApplicationDependencyProvider(this, default))
    AppDependencies.deadlockDetector.start()

    // Stage any test-declared remote config into the store to be read in RemoteConfig.init().
    if (TestRemoteConfig.pending.isNotEmpty()) {
      val json = TestRemoteConfig.json
      SignalStore.remoteConfig.currentConfig = json
      SignalStore.remoteConfig.pendingConfig = json
    }
  }

  override fun initializeLogging() {
    Log.initialize({ true }, AndroidLogger, PersistentLogger.getInstance(this), inMemoryLogger)

    SignalProtocolLoggerProvider.setProvider(CustomSignalProtocolLogger())

    SignalExecutors.UNBOUNDED.execute {
      Log.blockUntilAllWritesFinished()
      LogDatabase.getInstance(this).logs.trimToSize()
    }
  }

  override fun beginJobLoop() = Unit

  /**
   * Some of the jobs can interfere with some of the instrumentation tests.
   *
   * For example, we may try to create a release channel recipient while doing
   * an import/backup test.
   *
   * This can be used to start the job loop if needed for tests that rely on it.
   */
  fun beginJobLoopForTests() {
    super.beginJobLoop()
  }
}
