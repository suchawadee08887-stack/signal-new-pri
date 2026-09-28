/*
 * Copyright 2025 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.recipients.ui

import dev.chat.fork.messenger.recipients.PhoneNumber
import dev.chat.fork.messenger.recipients.RecipientId

sealed interface RecipientSelection {
  sealed interface HasId : RecipientSelection {
    val id: RecipientId
  }

  sealed interface HasPhone : RecipientSelection {
    val phone: PhoneNumber
  }

  data class WithId(override val id: RecipientId) : HasId
  data class WithPhone(override val phone: PhoneNumber) : HasPhone
  data class WithIdAndPhone(override val id: RecipientId, override val phone: PhoneNumber) : HasId, HasPhone
}
