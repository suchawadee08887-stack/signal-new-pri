/*
 * Copyright 2024 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.components.settings.app.backups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.signal.core.util.concurrent.SignalDispatchers
import org.signal.core.util.logging.Log
import dev.chat.fork.messenger.backup.DeletionState
import dev.chat.fork.messenger.backup.v2.BackupRepository
import dev.chat.fork.messenger.backup.v2.MessageBackupTier
import dev.chat.fork.messenger.database.SignalDatabase
import dev.chat.fork.messenger.keyvalue.SignalStore
import dev.chat.fork.messenger.recipients.Recipient
import dev.chat.fork.messenger.storage.StorageSyncHelper
import dev.chat.fork.messenger.util.Environment
import kotlin.time.Duration.Companion.milliseconds

class BackupsSettingsViewModel : ViewModel() {

  companion object {
    private val TAG = Log.tag(BackupsSettingsViewModel::class)
  }

  private val internalStateFlow: MutableStateFlow<BackupsSettingsState>

  val stateFlow: StateFlow<BackupsSettingsState> by lazy { internalStateFlow }

  init {
    val repo = BackupStateObserver(viewModelScope, useDatabaseFallbackOnNetworkError = true)
    internalStateFlow = MutableStateFlow(BackupsSettingsState(backupState = repo.backupState.value))

    viewModelScope.launch {
      repo.backupState.collect { enabledState ->
        Log.d(TAG, "Found enabled state $enabledState. Updating UI state.")
        internalStateFlow.update {
          it.copy(
            backupState = enabledState,
            lastBackupAt = SignalStore.backup.lastBackupTime.milliseconds,
            showBackupTierInternalOverride = Environment.IS_STAGING && SignalStore.account.isPrimaryDevice,
            backupTierInternalOverride = SignalStore.backup.backupTierInternalOverride
          )
        }
      }
    }

    viewModelScope.launch(Dispatchers.Default) {
      SignalStore.backup.lastBackupTimeFlow
        .collect { lastBackupTime ->
          internalStateFlow.update {
            it.copy(lastBackupAt = lastBackupTime.milliseconds)
          }
        }
    }

    if (SignalStore.account.isLinkedDevice) {
      viewModelScope.launch(Dispatchers.IO) {
        BackupRepository.refreshBackupFileTimestamp()
      }
    }
  }

  fun onBackupTierInternalOverrideChanged(tier: MessageBackupTier?) {
    SignalStore.backup.backupTierInternalOverride = tier
    SignalStore.backup.deletionState = DeletionState.NONE
    viewModelScope.launch(SignalDispatchers.Default) {
      SignalDatabase.recipients.markNeedsSync(Recipient.self().id)
      StorageSyncHelper.scheduleSyncForDataChange()
    }

    BackupStateObserver.notifyBackupStateChanged(scope = viewModelScope)
  }
}
