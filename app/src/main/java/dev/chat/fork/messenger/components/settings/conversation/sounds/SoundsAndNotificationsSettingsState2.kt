/*
 * Copyright 2025 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.components.settings.conversation.sounds

import dev.chat.fork.messenger.database.RecipientTable.NotificationSetting
import dev.chat.fork.messenger.recipients.Recipient
import dev.chat.fork.messenger.recipients.RecipientId

data class SoundsAndNotificationsSettingsState2(
  val recipientId: RecipientId = Recipient.UNKNOWN.id,
  val muteUntil: Long = 0L,
  val mentionSetting: NotificationSetting = NotificationSetting.ALWAYS_NOTIFY,
  val callNotificationSetting: NotificationSetting = NotificationSetting.ALWAYS_NOTIFY,
  val replyNotificationSetting: NotificationSetting = NotificationSetting.ALWAYS_NOTIFY,
  val hasCustomNotificationSettings: Boolean = false,
  val hasMentionsSupport: Boolean = false,
  val channelConsistencyCheckComplete: Boolean = false
) {
  val isMuted = muteUntil > 0
}
