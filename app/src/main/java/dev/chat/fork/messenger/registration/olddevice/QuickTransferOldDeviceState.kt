/*
 * Copyright 2026 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.registration.olddevice

import dev.chat.fork.messenger.registration.data.QuickRegistrationRepository
import org.whispersystems.signalservice.api.provisioning.RestoreMethod

data class QuickTransferOldDeviceState(
  val reRegisterUri: String,
  val inProgress: Boolean = false,
  val reRegisterResult: QuickRegistrationRepository.TransferAccountResult? = null,
  val restoreMethodSelected: RestoreMethod? = null,
  val navigateToBackupCreation: Boolean = false,
  val lastBackupTimestamp: Long = 0,
  val performAuthentication: Boolean = false
)
