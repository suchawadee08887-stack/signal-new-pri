package dev.chat.fork.messenger.messages

import android.graphics.Color
import org.signal.core.util.Base64
import org.signal.core.util.UuidUtil
import org.signal.core.util.orNull
import dev.chat.fork.messenger.database.MessageTable.InsertResult
import dev.chat.fork.messenger.database.MessageType
import dev.chat.fork.messenger.database.SignalDatabase
import dev.chat.fork.messenger.database.model.StoryType
import dev.chat.fork.messenger.database.model.databaseprotos.ChatColor
import dev.chat.fork.messenger.database.model.databaseprotos.StoryTextPost
import dev.chat.fork.messenger.database.model.toBodyRangeList
import dev.chat.fork.messenger.dependencies.AppDependencies
import dev.chat.fork.messenger.messages.MessageContentProcessor.Companion.log
import dev.chat.fork.messenger.messages.MessageContentProcessor.Companion.warn
import dev.chat.fork.messenger.messages.SignalServiceProtoUtil.groupId
import dev.chat.fork.messenger.messages.SignalServiceProtoUtil.toPointer
import dev.chat.fork.messenger.mms.IncomingMessage
import dev.chat.fork.messenger.mms.MmsException
import dev.chat.fork.messenger.recipients.Recipient
import dev.chat.fork.messenger.stories.Stories
import dev.chat.fork.messenger.util.RemoteConfig
import org.whispersystems.signalservice.api.crypto.EnvelopeMetadata
import org.whispersystems.signalservice.internal.push.Content
import org.whispersystems.signalservice.internal.push.Envelope
import org.whispersystems.signalservice.internal.push.StoryMessage
import org.whispersystems.signalservice.internal.push.TextAttachment
import org.whispersystems.signalservice.internal.util.Util

object StoryMessageProcessor {

  fun process(envelope: Envelope, content: Content, metadata: EnvelopeMetadata, senderRecipient: Recipient, threadRecipient: Recipient) {
    val storyMessage = content.storyMessage!!

    log(envelope.clientTimestamp!!, "Story message.")

    if (threadRecipient.isInactiveGroup) {
      warn(envelope.clientTimestamp!!, "Dropping a group story from a group we're no longer in.")
      return
    }

    if (threadRecipient.isGroup && !SignalDatabase.groups.isCurrentMember(threadRecipient.requireGroupId().requirePush(), senderRecipient.id)) {
      warn(envelope.clientTimestamp!!, "Dropping a group story from a user who's no longer a member.")
      return
    }

    if (threadRecipient.isGroup) {
      val groupRecord = SignalDatabase.groups.getGroup(threadRecipient.requireGroupId()).orNull()
      if (groupRecord != null && groupRecord.isAnnouncementGroup && !groupRecord.isAdmin(senderRecipient)) {
        warn(envelope.clientTimestamp!!, "Dropping a group story from a non-admin in an announcement-only group.")
        return
      }
    }

    if (!threadRecipient.isGroup && !(senderRecipient.isProfileSharing || senderRecipient.isSystemContact)) {
      warn(envelope.clientTimestamp!!, "Dropping story from an untrusted source.")
      return
    }

    val insertResult: InsertResult?

    SignalDatabase.messages.beginTransaction()

    try {
      val storyType: StoryType = if (storyMessage.allowsReplies == true) {
        StoryType.withReplies(isTextStory = storyMessage.textAttachment != null)
      } else {
        StoryType.withoutReplies(isTextStory = storyMessage.textAttachment != null)
      }

      val mediaMessage = IncomingMessage(
        type = MessageType.NORMAL,
        from = senderRecipient.id,
        sentTimeMillis = envelope.clientTimestamp!!,
        serverTimeMillis = envelope.serverTimestamp!!,
        receivedTimeMillis = System.currentTimeMillis(),
        storyType = storyType,
        isUnidentified = metadata.sealedSender,
        body = serializeTextAttachment(storyMessage),
        groupId = storyMessage.group?.groupId,
        attachments = listOfNotNull(storyMessage.fileAttachment?.toPointer()),
        linkPreviews = DataMessageProcessor.getLinkPreviews(
          previews = listOfNotNull(storyMessage.textAttachment?.preview),
          body = "",
          isStoryEmbed = true
        ),
        serverGuid = UuidUtil.getStringUUID(envelope.serverGuid, envelope.serverGuidBinary),
        messageRanges = storyMessage.bodyRanges.filter { Util.allAreNull(it.mentionAci, it.mentionAciBinary) }.toBodyRangeList()
      )

      insertResult = SignalDatabase.messages.insertMessageInbox(mediaMessage, -1).orNull()
      if (insertResult != null) {
        SignalDatabase.messages.setTransactionSuccessful()
      }
    } catch (e: MmsException) {
      throw StorageFailedException(e, metadata.sourceServiceId.toString(), metadata.sourceDeviceId)
    } finally {
      SignalDatabase.messages.endTransaction()
    }

    if (insertResult != null) {
      Stories.enqueueNextStoriesForDownload(threadRecipient.id, false, RemoteConfig.storiesAutoDownloadMaximum)
      AppDependencies.expireStoriesManager.scheduleIfNecessary()
    }
  }

  fun serializeTextAttachment(story: StoryMessage): String? {
    val textAttachment = story.textAttachment ?: return null
    val builder = StoryTextPost.Builder()

    if (textAttachment.text != null) {
      builder.body = textAttachment.text!!
    }

    when (textAttachment.textStyle) {
      TextAttachment.Style.DEFAULT -> builder.style = StoryTextPost.Style.DEFAULT
      TextAttachment.Style.REGULAR -> builder.style = StoryTextPost.Style.REGULAR
      TextAttachment.Style.BOLD -> builder.style = StoryTextPost.Style.BOLD
      TextAttachment.Style.SERIF -> builder.style = StoryTextPost.Style.SERIF
      TextAttachment.Style.SCRIPT -> builder.style = StoryTextPost.Style.SCRIPT
      TextAttachment.Style.CONDENSED -> builder.style = StoryTextPost.Style.CONDENSED
      null -> Unit
    }

    if (textAttachment.textBackgroundColor != null) {
      builder.textBackgroundColor = textAttachment.textBackgroundColor!!
    }

    if (textAttachment.textForegroundColor != null) {
      builder.textForegroundColor = textAttachment.textForegroundColor!!
    }

    val chatColorBuilder = ChatColor.Builder()

    if (textAttachment.color != null) {
      chatColorBuilder.singleColor(ChatColor.SingleColor.Builder().color(textAttachment.color!!).build())
    } else if (textAttachment.gradient != null) {
      val gradient = textAttachment.gradient!!
      val linearGradientBuilder = ChatColor.LinearGradient.Builder()
      linearGradientBuilder.rotation = (gradient.angle ?: 0).toFloat()

      if (gradient.positions.size > 1 && gradient.colors.size == gradient.positions.size) {
        val positions = ArrayList(gradient.positions)
        positions[0] = 0f
        positions[positions.size - 1] = 1f
        linearGradientBuilder.colors(ArrayList(gradient.colors))
        linearGradientBuilder.positions(positions)
      } else if (gradient.colors.isNotEmpty()) {
        warn("Incoming text story has color / position mismatch. Defaulting to start and end colors.")
        linearGradientBuilder.colors(listOf(gradient.colors[0], gradient.colors[gradient.colors.size - 1]))
        linearGradientBuilder.positions(listOf(0f, 1f))
      } else if (gradient.startColor != null && gradient.endColor != null) {
        warn("Incoming text story is using deprecated fields for the gradient. Building a two color gradient with them.")
        linearGradientBuilder.colors(listOf(gradient.startColor!!, gradient.endColor!!))
        linearGradientBuilder.positions(listOf(0f, 1f))
      } else {
        warn("Incoming text story did not have a valid linear gradient.")
        linearGradientBuilder.colors(listOf(Color.BLACK, Color.BLACK))
        linearGradientBuilder.positions(listOf(0f, 1f))
      }
      chatColorBuilder.linearGradient(linearGradientBuilder.build())
    }
    builder.background(chatColorBuilder.build())

    return Base64.encodeWithPadding(builder.build().encode())
  }
}
