package dev.chat.fork.messenger.database.model

import org.signal.libsignal.protocol.IdentityKey
import dev.chat.fork.messenger.database.IdentityTable
import dev.chat.fork.messenger.recipients.RecipientId

data class IdentityRecord(
  val recipientId: RecipientId,
  val identityKey: IdentityKey,
  val verifiedStatus: IdentityTable.VerifiedStatus,
  @get:JvmName("isFirstUse")
  val firstUse: Boolean,
  val timestamp: Long,
  @get:JvmName("isApprovedNonBlocking")
  val nonblockingApproval: Boolean
)
