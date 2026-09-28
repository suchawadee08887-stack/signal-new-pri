package dev.chat.fork.messenger.components.settings.app.chats.folders

import dev.chat.fork.messenger.recipients.Recipient

/**
 * Information needed when creating/updating a chat folder. Used in [ChatFoldersSettingsState]
 */
data class ChatFolder(
  val folderRecord: ChatFolderRecord = ChatFolderRecord(),
  val includedRecipients: Set<Recipient> = emptySet(),
  val excludedRecipients: Set<Recipient> = emptySet()
)
