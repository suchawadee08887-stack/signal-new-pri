package dev.chat.fork.messenger.logsubmit

import android.content.Context
import dev.chat.fork.messenger.database.SignalDatabase
import dev.chat.fork.messenger.keyvalue.SignalStore
import dev.chat.fork.messenger.notifications.profiles.NotificationProfile

class LogSectionNotificationProfiles : LogSection {
  override fun getTitle(): String = "NOTIFICATION PROFILES"

  override fun getContent(context: Context): CharSequence {
    val profiles: List<NotificationProfile> = SignalDatabase.notificationProfiles.getProfiles()

    val output = StringBuilder()

    output.append("Manually enabled profile: ${SignalStore.notificationProfile.manuallyEnabledProfile}\n")
    output.append("Manually enabled until  : ${SignalStore.notificationProfile.manuallyEnabledUntil}\n")
    output.append("Manually disabled at    : ${SignalStore.notificationProfile.manuallyDisabledAt}\n")
    output.append("Now                     : ${System.currentTimeMillis()}\n\n")

    output.append("Profiles:\n")
    if (profiles.isEmpty()) {
      output.append("  No notification profiles")
    } else {
      profiles.forEach { profile ->
        output.append("  Profile ${profile.id}\n")
        output.append("    allowMentions   : ${profile.allowAllMentions}\n")
        output.append("    allowCalls      : ${profile.allowAllCalls}\n")
        output.append("    schedule enabled: ${profile.schedule.enabled}\n")
        output.append("    schedule start  : ${profile.schedule.start}\n")
        output.append("    schedule end    : ${profile.schedule.end}\n")
        output.append("    schedule days   : ${profile.schedule.daysEnabled.sorted()}\n")
      }
    }

    return output.toString()
  }
}
