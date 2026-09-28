package dev.chat.fork.messenger.stories.viewer.views

import dev.chat.fork.messenger.recipients.Recipient

data class StoryViewItemData(
  val recipient: Recipient,
  val timeViewedInMillis: Long
)
