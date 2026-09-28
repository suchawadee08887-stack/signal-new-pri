/*
 * Copyright 2024 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.components.settings.app.backups

import dev.chat.fork.messenger.backup.v2.MessageBackupTier
import dev.chat.fork.messenger.keyvalue.SignalStore
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Screen state for top-level backups settings screen.
 */
data class BackupsSettingsState(
  val backupState: BackupState,
  val lastBackupAt: Duration = SignalStore.backup.lastBackupTime.milliseconds,
  val showBackupTierInternalOverride: Boolean = false,
  val backupTierInternalOverride: MessageBackupTier? = null,
  val isLinkedDevice: Boolean = SignalStore.account.isLinkedDevice
)
