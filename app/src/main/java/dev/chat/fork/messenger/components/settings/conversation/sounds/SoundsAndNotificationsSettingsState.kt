package dev.chat.fork.messenger.components.settings.conversation.sounds

import dev.chat.fork.messenger.database.RecipientTable
import dev.chat.fork.messenger.recipients.Recipient
import dev.chat.fork.messenger.recipients.RecipientId

data class SoundsAndNotificationsSettingsState(
  val recipientId: RecipientId = Recipient.UNKNOWN.id,
  val muteUntil: Long = 0L,
  val mentionSetting: RecipientTable.NotificationSetting = RecipientTable.NotificationSetting.DO_NOT_NOTIFY,
  val hasCustomNotificationSettings: Boolean = false,
  val hasMentionsSupport: Boolean = false,
  val channelConsistencyCheckComplete: Boolean = false
)
