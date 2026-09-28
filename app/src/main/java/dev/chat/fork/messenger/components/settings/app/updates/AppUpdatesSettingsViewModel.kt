/*
 * Copyright 2025 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.components.settings.app.updates

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import dev.chat.fork.messenger.keyvalue.SignalStore
import kotlin.time.Duration.Companion.milliseconds

class AppUpdatesSettingsViewModel : ViewModel() {
  private val internalState = MutableStateFlow(getState())

  val state: StateFlow<AppUpdatesSettingsState> = internalState

  fun refresh() {
    internalState.update { getState() }
  }

  private fun getState(): AppUpdatesSettingsState {
    return AppUpdatesSettingsState(
      lastCheckedTime = SignalStore.apkUpdate.lastSuccessfulCheck.milliseconds,
      autoUpdateEnabled = SignalStore.apkUpdate.autoUpdate
    )
  }
}
