package dev.chat.fork.messenger.mms

import dev.chat.fork.messenger.attachments.Attachment
import dev.chat.fork.messenger.contactshare.Contact
import dev.chat.fork.messenger.database.MessageType
import dev.chat.fork.messenger.database.model.Mention
import dev.chat.fork.messenger.database.model.ParentStoryId
import dev.chat.fork.messenger.database.model.StoryType
import dev.chat.fork.messenger.database.model.databaseprotos.BodyRangeList
import dev.chat.fork.messenger.database.model.databaseprotos.GV2UpdateDescription
import dev.chat.fork.messenger.database.model.databaseprotos.GiftBadge
import dev.chat.fork.messenger.database.model.databaseprotos.MessageExtras
import dev.chat.fork.messenger.groups.GroupId
import dev.chat.fork.messenger.linkpreview.LinkPreview
import dev.chat.fork.messenger.polls.Poll
import dev.chat.fork.messenger.recipients.RecipientId

class IncomingMessage(
  val type: MessageType,
  val from: RecipientId,
  val sentTimeMillis: Long,
  val serverTimeMillis: Long,
  val receivedTimeMillis: Long,
  val groupId: GroupId? = null,
  val groupContext: MessageGroupContext? = null,
  val body: String? = null,
  val storyType: StoryType = StoryType.NONE,
  val parentStoryId: ParentStoryId? = null,
  val isStoryReaction: Boolean = false,
  val subscriptionId: Int = -1,
  val expiresIn: Long = 0,
  val quote: QuoteModel? = null,
  val isUnidentified: Boolean = false,
  val isViewOnce: Boolean = false,
  val serverGuid: String? = null,
  val messageRanges: BodyRangeList? = null,
  attachments: List<Attachment> = emptyList(),
  sharedContacts: List<Contact> = emptyList(),
  linkPreviews: List<LinkPreview> = emptyList(),
  mentions: List<Mention> = emptyList(),
  val giftBadge: GiftBadge? = null,
  val messageExtras: MessageExtras? = null,
  val isNotifiable: Boolean = false,
  val poll: Poll? = null
) {

  val attachments: List<Attachment> = ArrayList(attachments)
  val sharedContacts: List<Contact> = ArrayList(sharedContacts)
  val linkPreviews: List<LinkPreview> = ArrayList(linkPreviews)
  val mentions: List<Mention> = ArrayList(mentions)

  val isGroupMessage: Boolean = groupId != null

  companion object {
    @JvmStatic
    fun identityUpdate(from: RecipientId, sentTimestamp: Long, groupId: GroupId?): IncomingMessage {
      return IncomingMessage(
        from = from,
        sentTimeMillis = sentTimestamp,
        serverTimeMillis = -1,
        receivedTimeMillis = sentTimestamp,
        groupId = groupId,
        type = MessageType.IDENTITY_UPDATE
      )
    }

    @JvmStatic
    fun identityVerified(from: RecipientId, sentTimestamp: Long, groupId: GroupId?): IncomingMessage {
      return IncomingMessage(
        from = from,
        sentTimeMillis = sentTimestamp,
        serverTimeMillis = -1,
        receivedTimeMillis = sentTimestamp,
        groupId = groupId,
        type = MessageType.IDENTITY_VERIFIED
      )
    }

    @JvmStatic
    fun identityDefault(from: RecipientId, sentTimestamp: Long, groupId: GroupId?): IncomingMessage {
      return IncomingMessage(
        from = from,
        sentTimeMillis = sentTimestamp,
        serverTimeMillis = -1,
        receivedTimeMillis = sentTimestamp,
        groupId = groupId,
        type = MessageType.IDENTITY_DEFAULT
      )
    }

    fun contactJoined(from: RecipientId, currentTime: Long): IncomingMessage {
      return IncomingMessage(
        from = from,
        sentTimeMillis = currentTime,
        serverTimeMillis = -1,
        receivedTimeMillis = currentTime,
        type = MessageType.CONTACT_JOINED
      )
    }

    fun groupUpdate(from: RecipientId, timestamp: Long, groupId: GroupId, update: GV2UpdateDescription, isNotifiable: Boolean, serverGuid: String?, receivedTime: Long? = null): IncomingMessage {
      val messageExtras = MessageExtras(gv2UpdateDescription = update)
      val groupContext = MessageGroupContext(update.gv2ChangeDescription!!)

      return IncomingMessage(
        from = from,
        sentTimeMillis = timestamp,
        receivedTimeMillis = receivedTime?.minus(1) ?: System.currentTimeMillis(),
        serverTimeMillis = timestamp,
        serverGuid = serverGuid,
        groupId = groupId,
        groupContext = groupContext,
        type = MessageType.GROUP_UPDATE,
        messageExtras = messageExtras,
        isNotifiable = isNotifiable
      )
    }
  }
}
