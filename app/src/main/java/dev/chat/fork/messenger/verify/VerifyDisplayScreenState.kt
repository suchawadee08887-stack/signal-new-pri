/*
 * Copyright 2026 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.verify

import dev.chat.fork.messenger.keyvalue.SignalStore
import dev.chat.fork.messenger.recipients.Recipient

data class VerifyDisplayScreenState(
  val isSafetyNumberVerified: Boolean,
  val isAutomaticVerificationVisible: Boolean = SignalStore.settings.automaticVerificationEnabled,
  val shouldDisplayVerifyAutomaticallyEducationSheet: Boolean = SignalStore.settings.automaticVerificationEnabled && !SignalStore.uiHints.hasSeenVerifyAutomaticallySheet(),
  val recipient: Recipient? = null,
  val fingerprintHolder: FingerprintHolder = FingerprintHolder.Uninitialised,
  val automaticVerificationStatus: AutomaticVerificationStatus = AutomaticVerificationStatus.NONE,
  val clipComparisonResult: ClipComparisonResult? = null,
  val scanComparisonResult: ScanComparisonResult? = null
) {
  sealed interface ClipComparisonResult {
    val submissionTime: Long

    data class NoDataInClipboard(override val submissionTime: Long = System.currentTimeMillis()) : ClipComparisonResult
    data class NoSafetyNumberInClipboard(override val submissionTime: Long = System.currentTimeMillis()) : ClipComparisonResult
    data class Success(override val submissionTime: Long = System.currentTimeMillis()) : ClipComparisonResult
    data class Failure(override val submissionTime: Long = System.currentTimeMillis()) : ClipComparisonResult
  }

  sealed interface ScanComparisonResult {
    val submissionTime: Long

    data class IncorrectFormat(override val submissionTime: Long = System.currentTimeMillis()) : ScanComparisonResult
    data class Success(override val submissionTime: Long = System.currentTimeMillis()) : ScanComparisonResult
    data class Failure(override val submissionTime: Long = System.currentTimeMillis()) : ScanComparisonResult
  }
}
