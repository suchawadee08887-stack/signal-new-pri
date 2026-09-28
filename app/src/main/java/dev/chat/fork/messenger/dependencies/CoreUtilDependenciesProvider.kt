/*
 * Copyright 2026 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.dependencies

import org.signal.core.util.CoreUtilDependencies
import dev.chat.fork.messenger.keyvalue.SignalStore
import dev.chat.fork.messenger.util.RemoteDeprecation

object CoreUtilDependenciesProvider : CoreUtilDependencies.Provider {
  override fun provideIsClientDeprecated(): Boolean {
    return SignalStore.misc.isClientDeprecated
  }

  override fun provideTimeUntilRemoteDeprecation(currentTime: Long): Long {
    return RemoteDeprecation.getTimeUntilDeprecation(currentTime)
  }
}
