/*
 * Copyright 2026 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.dependencies

import org.signal.core.ui.CoreUiDependencies
import dev.chat.fork.messenger.BuildConfig
import dev.chat.fork.messenger.keyvalue.SignalStore
import dev.chat.fork.messenger.util.TextSecurePreferences

object CoreUiDependenciesProvider : CoreUiDependencies.Provider {
  override fun providePackageId(): String {
    return BuildConfig.APPLICATION_ID
  }

  override fun provideIsIncognitoKeyboardEnabled(): Boolean {
    return TextSecurePreferences.isIncognitoKeyboardEnabled(AppDependencies.application)
  }

  override fun provideIsScreenSecurityEnabled(): Boolean {
    return TextSecurePreferences.isScreenSecurityEnabled(AppDependencies.application)
  }

  override fun provideForceSplitPane(): Boolean {
    return SignalStore.internal.forceSplitPane
  }
}
