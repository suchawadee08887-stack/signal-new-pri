/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.conversation.v2.items

import androidx.lifecycle.LifecycleOwner
import com.bumptech.glide.RequestManager
import dev.chat.fork.messenger.conversation.ConversationAdapter
import dev.chat.fork.messenger.conversation.ConversationItemDisplayMode
import dev.chat.fork.messenger.conversation.colors.Colorizer
import dev.chat.fork.messenger.conversation.mutiselect.MultiselectPart
import dev.chat.fork.messenger.database.model.MessageRecord

/**
 * Describes the Adapter "context" that would normally have been
 * visible to an inner class.
 */
interface V2ConversationContext {
  val lifecycleOwner: LifecycleOwner
  val requestManager: RequestManager
  val displayMode: ConversationItemDisplayMode
  val clickListener: ConversationAdapter.ItemClickListener
  val selectedItems: Set<MultiselectPart>
  val isMessageRequestAccepted: Boolean
  val searchQuery: String?
  val isParentInScroll: Boolean

  fun getChatColorsData(): ChatColorsDrawable.ChatColorsData

  fun onStartExpirationTimeout(messageRecord: MessageRecord)

  fun hasWallpaper(): Boolean
  fun getColorizer(): Colorizer
  fun getNextMessage(adapterPosition: Int): MessageRecord?
  fun getPreviousMessage(adapterPosition: Int): MessageRecord?
}
