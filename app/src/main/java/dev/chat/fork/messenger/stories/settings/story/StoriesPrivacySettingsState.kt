package dev.chat.fork.messenger.stories.settings.story

import dev.chat.fork.messenger.contacts.paged.ContactSearchData
import dev.chat.fork.messenger.stories.archive.StoryArchiveDuration

data class StoriesPrivacySettingsState(
  val areStoriesEnabled: Boolean,
  val areViewReceiptsEnabled: Boolean,
  val isUpdatingEnabledState: Boolean = false,
  val storyContactItems: List<ContactSearchData> = emptyList(),
  val userHasStories: Boolean = false,
  val isArchiveEnabled: Boolean = false,
  val archiveDuration: StoryArchiveDuration = StoryArchiveDuration.THIRTY_DAYS
)
