package dev.chat.fork.messenger.testing.incomingmessageobserver

import android.app.Application
import org.signal.benchmark.setup.NoOpJob
import org.signal.core.util.UptimeSleepTimer
import org.signal.libsignal.net.Network
import org.signal.network.config.SignalServiceConfiguration
import dev.chat.fork.messenger.dependencies.AppDependencies
import dev.chat.fork.messenger.dependencies.ApplicationDependencyProvider
import dev.chat.fork.messenger.dependencies.InstrumentationApplicationDependencyProvider
import dev.chat.fork.messenger.jobmanager.JobManager
import dev.chat.fork.messenger.jobs.JobManagerFactories
import org.whispersystems.signalservice.api.websocket.SignalWebSocket
import org.whispersystems.signalservice.internal.websocket.BenchmarkWebSocketConnection
import java.util.function.Supplier
import kotlin.time.Duration.Companion.seconds

/**
 * Dependency provider used by [dev.chat.fork.messenger.IncomingMessageObserverInstrumentationApplicationContext].
 * Composes [InstrumentationApplicationDependencyProvider] (so existing mocks for the account /
 * archive / donations / billing APIs are reused) and overrides:
 *
 * - the auth and unauth websocket factories with [BenchmarkWebSocketConnection], so tests can
 *   inject encrypted envelopes through the real ingest pipeline;
 * - the job manager, swapping the startup network jobs handled by [NoOpJob.replaceFactories]
 *   to no-ops so they can't fire against unstubbed mocks during a test.
 */
class IncomingMessageObserverDependencyProvider(
  private val application: Application,
  default: ApplicationDependencyProvider
) : AppDependencies.Provider by InstrumentationApplicationDependencyProvider(application, default) {

  override fun provideAuthWebSocket(
    signalServiceConfigurationSupplier: Supplier<SignalServiceConfiguration>,
    libSignalNetworkSupplier: Supplier<Network>
  ): SignalWebSocket.AuthenticatedWebSocket {
    return SignalWebSocket.AuthenticatedWebSocket(
      connectionFactory = { BenchmarkWebSocketConnection.createAuthInstance() },
      canConnect = { true },
      sleepTimer = UptimeSleepTimer(),
      disconnectTimeoutMs = 15.seconds.inWholeMilliseconds
    )
  }

  override fun provideUnauthWebSocket(
    signalServiceConfigurationSupplier: Supplier<SignalServiceConfiguration>,
    libSignalNetworkSupplier: Supplier<Network>
  ): SignalWebSocket.UnauthenticatedWebSocket {
    return SignalWebSocket.UnauthenticatedWebSocket(
      connectionFactory = { BenchmarkWebSocketConnection.createUnauthInstance() },
      canConnect = { true },
      sleepTimer = UptimeSleepTimer(),
      disconnectTimeoutMs = 15.seconds.inWholeMilliseconds
    )
  }

  override fun provideJobManager(configurationBuilder: JobManager.Configuration.Builder): JobManager {
    val config = configurationBuilder
      .setJobFactories(NoOpJob.replaceFactories(JobManagerFactories.getJobFactories(application)))
      .build()
    return JobManager(application, config)
  }
}
