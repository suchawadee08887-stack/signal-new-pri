package dev.chat.fork.messenger.stories.viewer.page

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import dev.chat.fork.messenger.recipients.RecipientId

@Parcelize
data class StoryViewerPageArgs(
  val recipientId: RecipientId,
  val initialStoryId: Long,
  val isOutgoingOnly: Boolean,
  val isJumpForwardToUnviewed: Boolean,
  val source: Source,
  val groupReplyStartPosition: Int,
  val isFromArchive: Boolean = false
) : Parcelable {
  enum class Source {
    UNKNOWN,
    NOTIFICATION,
    INFO_CONTEXT
  }
}
