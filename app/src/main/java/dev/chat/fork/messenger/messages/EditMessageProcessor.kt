package dev.chat.fork.messenger.messages

import android.content.Context
import org.signal.core.util.UuidUtil
import org.signal.core.util.orNull
import dev.chat.fork.messenger.database.MessageTable.InsertResult
import dev.chat.fork.messenger.database.MessageType
import dev.chat.fork.messenger.database.SignalDatabase
import dev.chat.fork.messenger.database.model.MessageId
import dev.chat.fork.messenger.database.model.MmsMessageRecord
import dev.chat.fork.messenger.database.model.databaseprotos.BodyRangeList
import dev.chat.fork.messenger.database.model.toBodyRangeList
import dev.chat.fork.messenger.database.withAttachments
import dev.chat.fork.messenger.dependencies.AppDependencies
import dev.chat.fork.messenger.groups.GroupId
import dev.chat.fork.messenger.jobs.AttachmentDownloadJob
import dev.chat.fork.messenger.jobs.PushProcessEarlyMessagesJob
import dev.chat.fork.messenger.messages.MessageContentProcessor.Companion.log
import dev.chat.fork.messenger.messages.MessageContentProcessor.Companion.warn
import dev.chat.fork.messenger.messages.SignalServiceProtoUtil.groupId
import dev.chat.fork.messenger.messages.SignalServiceProtoUtil.isMediaMessage
import dev.chat.fork.messenger.messages.SignalServiceProtoUtil.toPointersWithinLimit
import dev.chat.fork.messenger.mms.IncomingMessage
import dev.chat.fork.messenger.mms.QuoteModel
import dev.chat.fork.messenger.notifications.v2.ConversationId.Companion.forConversation
import dev.chat.fork.messenger.recipients.Recipient
import dev.chat.fork.messenger.recipients.RecipientId
import dev.chat.fork.messenger.util.EarlyMessageCacheEntry
import dev.chat.fork.messenger.util.MediaUtil
import dev.chat.fork.messenger.util.MessageConstraintsUtil
import dev.chat.fork.messenger.util.hasAudio
import dev.chat.fork.messenger.util.hasSharedContact
import org.whispersystems.signalservice.api.crypto.EnvelopeMetadata
import org.whispersystems.signalservice.internal.push.Content
import org.whispersystems.signalservice.internal.push.DataMessage
import org.whispersystems.signalservice.internal.push.Envelope
import org.whispersystems.signalservice.internal.util.Util

object EditMessageProcessor {
  fun process(
    context: Context,
    senderRecipient: Recipient,
    threadRecipient: Recipient,
    envelope: Envelope,
    content: Content,
    metadata: EnvelopeMetadata,
    earlyMessageCacheEntry: EarlyMessageCacheEntry?,
    batchCache: BatchCache
  ) {
    val editMessage = content.editMessage!!

    log(envelope.clientTimestamp!!, "[handleEditMessage] Edit message for " + editMessage.targetSentTimestamp)

    var targetMessage: MmsMessageRecord? = SignalDatabase.messages.getMessageFor(editMessage.targetSentTimestamp!!, senderRecipient.id) as? MmsMessageRecord
    val targetThreadRecipient: Recipient? = if (targetMessage != null) SignalDatabase.threads.getRecipientForThreadId(targetMessage.threadId) else null

    if (targetMessage == null || targetThreadRecipient == null) {
      warn(envelope.clientTimestamp!!, "[handleEditMessage] Could not find matching message! timestamp: ${editMessage.targetSentTimestamp}  author: ${senderRecipient.id}")

      if (earlyMessageCacheEntry != null) {
        AppDependencies.earlyMessageCache.store(senderRecipient.id, editMessage.targetSentTimestamp!!, earlyMessageCacheEntry)
        PushProcessEarlyMessagesJob.enqueue()
      }

      return
    }

    val message = editMessage.dataMessage!!
    val isMediaMessage = message.isMediaMessage
    val groupId: GroupId.V2? = message.groupV2?.groupId

    val originalMessageWithoutAttachments = targetMessage.originalMessageId?.let { SignalDatabase.messages.getMessageRecord(it.id) } ?: targetMessage
    val originalMessage = originalMessageWithoutAttachments.withAttachments()
    val validTiming = MessageConstraintsUtil.isValidEditMessageReceive(originalMessage, senderRecipient, envelope.serverTimestamp!!)
    val validAuthor = senderRecipient.id == originalMessage.fromRecipient.id
    val validGroup = groupId == targetThreadRecipient.groupId.orNull()
    val validTarget = !originalMessage.isViewOnce && !originalMessage.hasAudio() && !originalMessage.hasSharedContact()

    if (!validTiming || !validAuthor || !validGroup || !validTarget) {
      warn(envelope.clientTimestamp!!, "[handleEditMessage] Invalid message edit! editTime: ${envelope.serverTimestamp}, targetTime: ${originalMessage.serverTimestamp}, editAuthor: ${senderRecipient.id}, targetAuthor: ${originalMessage.fromRecipient.id}, editThread: ${threadRecipient.id}, targetThread: ${targetThreadRecipient.id}, validity: (timing: $validTiming, author: $validAuthor, group: $validGroup, target: $validTarget)")
      return
    }

    if (groupId != null && MessageContentProcessor.handleGv2PreProcessing(context, envelope.clientTimestamp!!, content, metadata, groupId, message.groupV2!!, senderRecipient) == MessageContentProcessor.Gv2PreProcessResult.IGNORE) {
      warn(envelope.clientTimestamp!!, "[handleEditMessage] Group processor indicated we should ignore this.")
      return
    }

    DataMessageProcessor.notifyTypingStoppedFromIncomingMessage(context, senderRecipient, threadRecipient.id, metadata.sourceDeviceId)

    targetMessage = targetMessage.withAttachments(SignalDatabase.attachments.getAttachmentsForMessage(targetMessage.id))

    val insertResult: InsertResult? = if (isMediaMessage || targetMessage.quote != null || targetMessage.slideDeck.slides.isNotEmpty()) {
      handleEditMediaMessage(senderRecipient.id, groupId, envelope, metadata, message, targetMessage, batchCache)
    } else {
      handleEditTextMessage(senderRecipient.id, groupId, envelope, metadata, message, targetMessage, batchCache)
    }

    if (insertResult != null) {
      batchCache.addDeliveryReceipt(senderRecipient.id, groupId, message.timestamp!!, MessageId(insertResult.messageId))

      if (insertResult.needsThreadUpdate) {
        batchCache.addIncomingMessageInsertThreadUpdate(insertResult.threadId)
      }

      if (targetMessage.expireStarted > 0) {
        AppDependencies.expiringMessageManager
          .scheduleDeletion(
            insertResult.messageId,
            true,
            targetMessage.expireStarted,
            targetMessage.expiresIn
          )
      }

      AppDependencies.messageNotifier.updateNotification(context, forConversation(insertResult.threadId))
    }
  }

  private fun handleEditMediaMessage(
    senderRecipientId: RecipientId,
    groupId: GroupId.V2?,
    envelope: Envelope,
    metadata: EnvelopeMetadata,
    message: DataMessage,
    targetMessage: MmsMessageRecord,
    batchCache: BatchCache
  ): InsertResult? {
    val cappedBodyRanges = message.bodyRanges.take(DataMessageProcessor.BODY_RANGE_PROCESSING_LIMIT)
    val messageRanges: BodyRangeList? = cappedBodyRanges.filter { Util.allAreNull(it.mentionAci, it.mentionAciBinary) }.toList().toBodyRangeList()
    val targetQuote = targetMessage.quote
    val quote: QuoteModel? = if (targetQuote != null && (message.quote != null || (targetMessage.parentStoryId != null && message.storyContext != null))) {
      QuoteModel(
        targetQuote.id,
        targetQuote.author,
        targetQuote.displayText.toString(),
        targetQuote.isOriginalMissing,
        null,
        null,
        targetQuote.quoteType,
        null
      )
    } else {
      null
    }
    val attachments = message.attachments.toPointersWithinLimit().filter {
      MediaUtil.SlideType.LONG_TEXT == MediaUtil.getSlideTypeFromContentType(it.contentType)
    }
    val mediaMessage = IncomingMessage(
      type = MessageType.NORMAL,
      from = senderRecipientId,
      sentTimeMillis = message.timestamp!!,
      serverTimeMillis = envelope.serverTimestamp!!,
      receivedTimeMillis = targetMessage.dateReceived,
      expiresIn = targetMessage.expiresIn,
      isViewOnce = message.isViewOnce == true,
      isUnidentified = metadata.sealedSender,
      body = message.body,
      groupId = groupId,
      attachments = attachments,
      quote = quote,
      parentStoryId = targetMessage.parentStoryId,
      sharedContacts = emptyList(),
      linkPreviews = DataMessageProcessor.getLinkPreviews(message.preview, message.body ?: "", false),
      mentions = DataMessageProcessor.getMentions(cappedBodyRanges),
      serverGuid = UuidUtil.getStringUUID(envelope.serverGuid, envelope.serverGuidBinary),
      messageRanges = messageRanges
    )

    val insertResult = SignalDatabase.messages.insertEditMessageInbox(mediaMessage, targetMessage, skipThreadUpdate = batchCache.batchThreadUpdates).orNull()
    if (insertResult?.insertedAttachments != null) {
      SignalDatabase.runPostSuccessfulTransaction {
        val downloadJobs: List<AttachmentDownloadJob> = insertResult.insertedAttachments.mapNotNull { (_, attachmentId) ->
          AttachmentDownloadJob(insertResult.messageId, attachmentId, false)
        }
        AppDependencies.jobManager.addAll(downloadJobs)
      }
    }
    return insertResult
  }

  private fun handleEditTextMessage(
    senderRecipientId: RecipientId,
    groupId: GroupId.V2?,
    envelope: Envelope,
    metadata: EnvelopeMetadata,
    message: DataMessage,
    targetMessage: MmsMessageRecord,
    batchCache: BatchCache
  ): InsertResult? {
    val textMessage = IncomingMessage(
      type = MessageType.NORMAL,
      from = senderRecipientId,
      sentTimeMillis = envelope.clientTimestamp!!,
      serverTimeMillis = envelope.clientTimestamp!!,
      receivedTimeMillis = targetMessage.dateReceived,
      body = message.body,
      groupId = groupId,
      parentStoryId = targetMessage.parentStoryId,
      expiresIn = targetMessage.expiresIn,
      isUnidentified = metadata.sealedSender,
      serverGuid = UuidUtil.getStringUUID(envelope.serverGuid, envelope.serverGuidBinary)
    )

    return SignalDatabase.messages.insertEditMessageInbox(textMessage, targetMessage, skipThreadUpdate = batchCache.batchThreadUpdates).orNull()
  }
}
