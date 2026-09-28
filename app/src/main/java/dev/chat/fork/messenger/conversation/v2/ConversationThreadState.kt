/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.conversation.v2

import org.signal.paging.ObservablePagedData
import dev.chat.fork.messenger.conversation.ConversationData
import dev.chat.fork.messenger.conversation.v2.data.ConversationElementKey
import dev.chat.fork.messenger.util.adapter.mapping.MappingModel

/**
 * Represents the content that will be displayed in the conversation
 * thread (recycler).
 */
class ConversationThreadState(
  val items: ObservablePagedData<ConversationElementKey, MappingModel<*>>,
  val meta: ConversationData
)
