/*
 * Copyright 2026 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.conversation.mutiselect.forward

import androidx.compose.runtime.annotation.RememberInComposition
import dev.chat.fork.messenger.color.ViewColorSet
import dev.chat.fork.messenger.recipients.Recipient

data class MultiselectForwardBottomBarState @RememberInComposition constructor(
  val selection: List<SelectedContact> = emptyList(),
  val message: String = "",
  val sendButtonColors: ViewColorSet = ViewColorSet.PRIMARY,
  val isSendButtonEnabled: Boolean = true,
  val isSendButtonVisible: Boolean = false,
  val isAddMessageVisible: Boolean = false
) {
  sealed interface SelectedContact {

    val key: String

    data class KnownRecipient(val recipient: Recipient) : SelectedContact {
      override val key: String = recipient.id.toString()
    }

    data class UnknownRecipient(val e164: String) : SelectedContact {
      override val key: String = e164
    }
  }
}
