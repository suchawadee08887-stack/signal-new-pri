package dev.chat.fork.messenger.conversation

import dev.chat.fork.messenger.recipients.RecipientId

data class ConversationSecurityInfo(
  val recipientId: RecipientId = RecipientId.UNKNOWN,
  val isPushAvailable: Boolean = false,
  val isDefaultSmsApplication: Boolean = false,
  val isInitialized: Boolean = false,
  val hasUnexportedInsecureMessages: Boolean = false,
  val isClientExpired: Boolean = false,
  val isUnauthorized: Boolean = false
)
