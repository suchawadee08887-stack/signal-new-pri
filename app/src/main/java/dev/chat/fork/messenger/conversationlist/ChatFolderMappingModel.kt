package dev.chat.fork.messenger.conversationlist

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import dev.chat.fork.messenger.components.settings.app.chats.folders.ChatFolderRecord
import dev.chat.fork.messenger.util.adapter.mapping.MappingModel

/**
 * Mapping model of folders used in [ChatFolderAdapter]
 */
@Parcelize
data class ChatFolderMappingModel(
  val chatFolder: ChatFolderRecord,
  val unreadCount: Int,
  val isEmpty: Boolean,
  val isMuted: Boolean,
  val isSelected: Boolean
) : MappingModel<ChatFolderMappingModel>, Parcelable {
  override fun areItemsTheSame(newItem: ChatFolderMappingModel): Boolean {
    return chatFolder.id == newItem.chatFolder.id
  }

  override fun areContentsTheSame(newItem: ChatFolderMappingModel): Boolean {
    return chatFolder == newItem.chatFolder &&
      unreadCount == newItem.unreadCount &&
      isEmpty == newItem.isEmpty &&
      isMuted == newItem.isMuted &&
      isSelected == newItem.isSelected
  }
}
