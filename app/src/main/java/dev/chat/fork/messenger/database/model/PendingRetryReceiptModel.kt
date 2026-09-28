package dev.chat.fork.messenger.database.model

import dev.chat.fork.messenger.recipients.RecipientId

/** A model for [dev.chat.fork.messenger.database.PendingRetryReceiptTable] */
data class PendingRetryReceiptModel(
  val id: Long,
  val author: RecipientId,
  val authorDevice: Int,
  val sentTimestamp: Long,
  val receivedTimestamp: Long,
  val threadId: Long
)
