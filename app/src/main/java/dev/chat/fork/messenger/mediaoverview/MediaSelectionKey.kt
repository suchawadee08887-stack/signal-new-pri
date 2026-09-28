package dev.chat.fork.messenger.mediaoverview

import org.signal.core.models.database.AttachmentId
import dev.chat.fork.messenger.database.MediaTable.MediaRecord

sealed class MediaSelectionKey {
  data class Attachment(val attachmentId: AttachmentId) : MediaSelectionKey()
  data class Message(val messageId: Long) : MediaSelectionKey()

  companion object {
    @JvmStatic
    fun from(mediaRecord: MediaRecord): MediaSelectionKey {
      val attachment = mediaRecord.attachment
      return if (attachment != null) {
        Attachment(attachment.attachmentId)
      } else {
        Message(mediaRecord.messageId)
      }
    }
  }
}
