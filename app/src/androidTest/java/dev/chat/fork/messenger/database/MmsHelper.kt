package dev.chat.fork.messenger.database

import dev.chat.fork.messenger.database.model.ParentStoryId
import dev.chat.fork.messenger.database.model.StoryType
import dev.chat.fork.messenger.database.model.databaseprotos.GiftBadge
import dev.chat.fork.messenger.mms.IncomingMessage
import dev.chat.fork.messenger.mms.OutgoingMessage
import dev.chat.fork.messenger.recipients.Recipient
import java.util.Optional

/**
 * Helper methods for inserting an MMS message into the MMS table.
 */
object MmsHelper {

  fun insert(
    recipient: Recipient = Recipient.UNKNOWN,
    body: String = "body",
    sentTimeMillis: Long = System.currentTimeMillis(),
    expiresIn: Long = 0,
    viewOnce: Boolean = false,
    distributionType: Int = ThreadTable.DistributionTypes.DEFAULT,
    threadId: Long = SignalDatabase.threads.getOrCreateThreadIdFor(recipient, distributionType),
    storyType: StoryType = StoryType.NONE,
    parentStoryId: ParentStoryId? = null,
    isStoryReaction: Boolean = false,
    giftBadge: GiftBadge? = null,
    secure: Boolean = true
  ): Long {
    val message = OutgoingMessage(
      recipient = recipient,
      body = body,
      timestamp = sentTimeMillis,
      expiresIn = expiresIn,
      viewOnce = viewOnce,
      distributionType = distributionType,
      storyType = storyType,
      parentStoryId = parentStoryId,
      isStoryReaction = isStoryReaction,
      giftBadge = giftBadge,
      isSecure = secure
    )

    return insert(
      message = message,
      threadId = threadId
    )
  }

  fun insert(
    message: OutgoingMessage,
    threadId: Long
  ): Long {
    return SignalDatabase.messages.insertMessageOutbox(message, threadId, false, GroupReceiptTable.STATUS_UNKNOWN, null).messageId
  }

  fun insert(
    message: IncomingMessage,
    threadId: Long
  ): Optional<MessageTable.InsertResult> {
    return SignalDatabase.messages.insertMessageInbox(message, threadId)
  }
}
