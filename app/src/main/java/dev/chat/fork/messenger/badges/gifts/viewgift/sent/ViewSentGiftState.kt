package dev.chat.fork.messenger.badges.gifts.viewgift.sent

import dev.chat.fork.messenger.badges.models.Badge
import dev.chat.fork.messenger.recipients.Recipient

data class ViewSentGiftState(
  val recipient: Recipient? = null,
  val badge: Badge? = null
)
