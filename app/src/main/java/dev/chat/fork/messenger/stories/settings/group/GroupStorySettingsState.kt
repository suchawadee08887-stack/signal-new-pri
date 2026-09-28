package dev.chat.fork.messenger.stories.settings.group

import dev.chat.fork.messenger.recipients.Recipient

data class GroupStorySettingsState(
  val name: String = "",
  val members: List<Recipient> = emptyList(),
  val removed: Boolean = false
)
