package dev.chat.fork.messenger.components.settings.app.internal

import android.content.Context
import org.json.JSONObject
import org.signal.core.util.concurrent.SignalExecutors
import org.signal.donations.InAppPaymentType
import dev.chat.fork.messenger.database.MessageTable
import dev.chat.fork.messenger.database.SignalDatabase
import dev.chat.fork.messenger.database.model.RemoteMegaphoneRecord
import dev.chat.fork.messenger.database.model.addButton
import dev.chat.fork.messenger.database.model.addLink
import dev.chat.fork.messenger.database.model.addStyle
import dev.chat.fork.messenger.database.model.databaseprotos.BodyRangeList
import dev.chat.fork.messenger.dependencies.AppDependencies
import dev.chat.fork.messenger.emoji.EmojiFiles
import dev.chat.fork.messenger.jobs.AttachmentDownloadJob
import dev.chat.fork.messenger.jobs.CreateReleaseChannelJob
import dev.chat.fork.messenger.jobs.FetchRemoteMegaphoneImageJob
import dev.chat.fork.messenger.jobs.InAppPaymentRecurringContextJob
import dev.chat.fork.messenger.keyvalue.SignalStore
import dev.chat.fork.messenger.notifications.v2.ConversationId
import dev.chat.fork.messenger.recipients.Recipient
import dev.chat.fork.messenger.releasechannel.ReleaseChannel
import java.util.UUID
import kotlin.time.Duration.Companion.days

class InternalSettingsRepository(context: Context) {

  private val context = context.applicationContext

  fun getEmojiVersionInfo(consumer: (EmojiFiles.Version?) -> Unit) {
    SignalExecutors.BOUNDED.execute {
      consumer(EmojiFiles.Version.readVersion(context))
    }
  }

  fun enqueueSubscriptionRedemption() {
    SignalExecutors.BOUNDED.execute {
      val latest = SignalDatabase.inAppPayments.getByLatestEndOfPeriod(InAppPaymentType.RECURRING_DONATION)
      if (latest != null) {
        InAppPaymentRecurringContextJob.createJobChain(latest).enqueue()
      }
    }
  }

  fun addSampleReleaseNote(callToAction: String) {
    SignalExecutors.UNBOUNDED.execute {
      AppDependencies.jobManager.runSynchronously(CreateReleaseChannelJob.create(), 5000)

      val title = "Release Note Title"
      val bodyText = "Release note body. Aren't I awesome?"
      val linkUrl = "https://signal.org"
      val body = "$title\n\n$bodyText\n\n$linkUrl"
      val linkStart = body.length - linkUrl.length
      val bodyRangeList = BodyRangeList.Builder()
        .addStyle(BodyRangeList.BodyRange.Style.BOLD, 0, title.length)
        .addLink(linkUrl, linkStart, linkUrl.length)

      bodyRangeList.addButton("Call to Action Text", callToAction, body.lastIndex, 0)

      val recipientId = SignalStore.releaseChannel.releaseChannelRecipientId!!
      val threadId = SignalDatabase.threads.getOrCreateThreadIdFor(Recipient.resolved(recipientId))

      val insertResult: MessageTable.InsertResult? = ReleaseChannel.insertReleaseChannelMessage(
        recipientId = recipientId,
        body = body,
        threadId = threadId,
        messageRanges = bodyRangeList.build(),
        media = "/static/release-notes/signal.png",
        mediaWidth = 1800,
        mediaHeight = 720
      )

      SignalDatabase.messages.insertBoostRequestMessage(recipientId, threadId)

      if (insertResult != null) {
        SignalDatabase.attachments.getAttachmentsForMessage(insertResult.messageId)
          .forEach { AppDependencies.jobManager.add(AttachmentDownloadJob(insertResult.messageId, it.attachmentId, false)) }

        AppDependencies.messageNotifier.updateNotification(context, ConversationId.forConversation(insertResult.threadId))
      }
    }
  }

  fun addRemoteMegaphone(actionId: RemoteMegaphoneRecord.ActionId) {
    SignalExecutors.UNBOUNDED.execute {
      val record = RemoteMegaphoneRecord(
        uuid = UUID.randomUUID().toString(),
        priority = 100,
        countries = "*:1000000",
        minimumVersion = 1,
        doNotShowBefore = System.currentTimeMillis() - 2.days.inWholeMilliseconds,
        doNotShowAfter = System.currentTimeMillis() + 28.days.inWholeMilliseconds,
        showForNumberOfDays = 30,
        conditionalId = null,
        primaryActionId = actionId,
        secondaryActionId = RemoteMegaphoneRecord.ActionId.SNOOZE,
        imageUrl = "/static/release-notes/donate-heart.png",
        title = "Donate Test",
        body = "Donate body test.",
        primaryActionText = "Donate",
        secondaryActionText = "Snooze",
        primaryActionData = null,
        secondaryActionData = JSONObject("{ \"snoozeDurationDays\": [5, 7, 100] }")
      )

      SignalDatabase.remoteMegaphones.insert(record)

      if (record.imageUrl != null) {
        AppDependencies.jobManager.add(FetchRemoteMegaphoneImageJob(record.uuid, record.imageUrl))
      }
    }
  }
}
