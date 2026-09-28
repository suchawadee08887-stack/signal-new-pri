/*
 * Copyright 2026 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.components.settings.conversation

import androidx.fragment.app.FragmentActivity
import dev.chat.fork.messenger.main.MainNavigationChatDetailRouter
import dev.chat.fork.messenger.main.MainNavigationDetailLocation
import dev.chat.fork.messenger.recipients.Recipient

/**
 * Routes to the conversation settings screen, handling split-pane vs. standalone activity automatically.
 */
object ConversationSettingsNavigator {
  @JvmStatic
  fun navigate(
    activity: FragmentActivity,
    recipient: Recipient
  ) {
    if (activity is MainNavigationChatDetailRouter) {
      activity.goToChatDetail(MainNavigationDetailLocation.Chats.ConversationSettings(recipient.id))
      return
    }

    val intent = if (recipient.isPushGroup) {
      ConversationSettingsActivity.forGroup(activity, recipient.requireGroupId())
    } else {
      ConversationSettingsActivity.forRecipient(activity, recipient.id)
    }
    activity.startActivity(intent)
  }
}
