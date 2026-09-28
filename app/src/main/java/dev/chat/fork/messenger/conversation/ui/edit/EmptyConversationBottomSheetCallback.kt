/*
 * Copyright 2026 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.conversation.ui.edit

import dev.chat.fork.messenger.conversation.ConversationAdapter
import dev.chat.fork.messenger.conversation.ConversationBottomSheetCallback
import dev.chat.fork.messenger.conversation.ConversationMessage
import dev.chat.fork.messenger.database.model.MessageRecord

object EmptyConversationBottomSheetCallback : ConversationBottomSheetCallback {
  override fun getConversationAdapterListener(): ConversationAdapter.ItemClickListener = EmptyConversationAdapterListener
  override fun jumpToMessage(messageRecord: MessageRecord) = Unit
  override fun unpin(conversationMessage: ConversationMessage) = Unit
  override fun copy(conversationMessage: ConversationMessage) = Unit
  override fun delete(conversationMessage: ConversationMessage) = Unit
  override fun save(conversationMessage: ConversationMessage) = Unit
}
