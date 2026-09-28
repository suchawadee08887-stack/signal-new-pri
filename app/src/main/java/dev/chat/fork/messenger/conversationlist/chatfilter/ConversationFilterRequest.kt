package dev.chat.fork.messenger.conversationlist.chatfilter

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import dev.chat.fork.messenger.conversationlist.model.ConversationFilter

@Parcelize
data class ConversationFilterRequest(
  val filter: ConversationFilter,
  val source: ConversationFilterSource
) : Parcelable
