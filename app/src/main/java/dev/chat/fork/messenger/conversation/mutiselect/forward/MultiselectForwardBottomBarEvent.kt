/*
 * Copyright 2026 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.conversation.mutiselect.forward

sealed interface MultiselectForwardBottomBarEvent {
  data class AddMessageUpdate(val message: String) : MultiselectForwardBottomBarEvent
  data object SendClick : MultiselectForwardBottomBarEvent
}
