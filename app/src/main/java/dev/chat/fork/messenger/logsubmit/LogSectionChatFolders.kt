package dev.chat.fork.messenger.logsubmit

import android.content.Context
import dev.chat.fork.messenger.components.settings.app.chats.folders.ChatFolderRecord
import dev.chat.fork.messenger.database.SignalDatabase
import dev.chat.fork.messenger.recipients.Recipient

/**
 * Prints out chat folders settings
 */
class LogSectionChatFolders : LogSection {
  override fun getTitle(): String = "CHAT FOLDERS"

  override fun getContent(context: Context): CharSequence {
    val output = StringBuilder()

    if (Recipient.isSelfSet) {
      val count = SignalDatabase.chatFolders.getFolderCount()
      val hasDefault = SignalDatabase.chatFolders.getCurrentChatFolders().any { folder -> folder.folderType == ChatFolderRecord.FolderType.ALL }
      output.append("Has default all chats         : ${hasDefault}\n")
      output.append("Number of folders (undeleted) : ${count}\n")
    } else {
      output.append("< Self is not set yet >\n")
    }

    return output
  }
}
