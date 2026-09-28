/*
 * Copyright 2026 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger

import android.app.Application
import org.signal.benchmark.setup.NoOpJob
import org.signal.core.util.UptimeSleepTimer
import org.signal.libsignal.net.Network
import org.signal.network.config.SignalServiceConfiguration
import dev.chat.fork.messenger.dependencies.AppDependencies
import dev.chat.fork.messenger.dependencies.ApplicationDependencyProvider
import dev.chat.fork.messenger.jobmanager.JobManager
import dev.chat.fork.messenger.jobs.JobManagerFactories
import dev.chat.fork.messenger.net.DeviceTransferBlockingInterceptor
import org.whispersystems.signalservice.api.websocket.SignalWebSocket
import org.whispersystems.signalservice.internal.websocket.BenchmarkWebSocketConnection
import java.util.function.Supplier
import kotlin.time.Duration.Companion.seconds

class BenchmarkApplicationContext : ApplicationContext() {

  override fun initializeAppDependencies() {
    AppDependencies.init(this, BenchmarkDependencyProvider(this, ApplicationDependencyProvider(this)))

    DeviceTransferBlockingInterceptor.getInstance().blockNetwork()
  }

  override fun onForeground() = Unit

  class BenchmarkDependencyProvider(val application: Application, private val default: ApplicationDependencyProvider) : AppDependencies.Provider by default {
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
}
