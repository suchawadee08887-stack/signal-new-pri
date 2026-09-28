package dev.chat.fork.messenger.stories.viewer.reply.direct

import dev.chat.fork.messenger.database.model.MessageRecord
import dev.chat.fork.messenger.recipients.Recipient

data class StoryDirectReplyState(
  val groupDirectReplyRecipient: Recipient? = null,
  val storyRecord: MessageRecord? = null
)
