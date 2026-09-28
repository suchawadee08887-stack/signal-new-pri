/*
 * Copyright 2024 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.registration.ui.reregisterwithpin

import dev.chat.fork.messenger.keyvalue.SignalStore
import dev.chat.fork.messenger.lock.v2.PinKeyboardType

data class ReRegisterWithPinState(
  val isLocalVerification: Boolean = false,
  val hasIncorrectGuess: Boolean = false,
  val localPinMatches: Boolean = false,
  val pinKeyboardType: PinKeyboardType = SignalStore.pin.keyboardType
)
