package dev.chat.fork.messenger

import org.signal.core.util.logging.AndroidLogger
import org.signal.core.util.logging.Log
import org.signal.libsignal.protocol.logging.SignalProtocolLoggerProvider
import dev.chat.fork.messenger.dependencies.AppDependencies
import dev.chat.fork.messenger.dependencies.ApplicationDependencyProvider
import dev.chat.fork.messenger.logging.CustomSignalProtocolLogger
import dev.chat.fork.messenger.testing.incomingmessageobserver.IncomingMessageObserverDependencyProvider
import dev.chat.fork.messenger.testing.incomingmessageobserver.IncomingMessageObserverTestRunner

/**
 * Application used when running `IncomingMessageObserver` instrumentation tests. Installs
 * [IncomingMessageObserverDependencyProvider] so the websocket and job manager are replaced
 * with test-friendly implementations. Selected by [IncomingMessageObserverTestRunner] when
 * gradle is invoked with `-PimoTests`.
 */
class IncomingMessageObserverInstrumentationApplicationContext : ApplicationContext() {

  override fun initializeAppDependencies() {
    val default = ApplicationDependencyProvider(this)
    AppDependencies.init(this, IncomingMessageObserverDependencyProvider(this, default))
    AppDependencies.deadlockDetector.start()
  }

  override fun initializeLogging() {
    Log.initialize({ true }, AndroidLogger)
    SignalProtocolLoggerProvider.setProvider(CustomSignalProtocolLogger())
  }

  override fun beginJobLoop() = Unit

  fun beginJobLoopForTests() {
    super.beginJobLoop()
  }
}
