package dev.chat.fork.messenger.components.settings.app.notifications.profiles

import dev.chat.fork.messenger.notifications.profiles.NotificationProfile
import dev.chat.fork.messenger.notifications.profiles.NotificationProfiles

data class NotificationProfilesState(
  val profiles: List<NotificationProfile>,
  val activeProfile: NotificationProfile? = NotificationProfiles.getActiveProfile(profiles)
)
