package dev.chat.fork.messenger.stories.viewer.reply.direct

import android.content.Context
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import dev.chat.fork.messenger.database.SignalDatabase
import dev.chat.fork.messenger.database.model.MessageRecord
import dev.chat.fork.messenger.database.model.MmsMessageRecord
import dev.chat.fork.messenger.database.model.ParentStoryId
import dev.chat.fork.messenger.database.model.databaseprotos.BodyRangeList
import dev.chat.fork.messenger.database.withAttachments
import dev.chat.fork.messenger.mms.OutgoingMessage
import dev.chat.fork.messenger.mms.QuoteModel
import dev.chat.fork.messenger.recipients.Recipient
import dev.chat.fork.messenger.recipients.RecipientId
import dev.chat.fork.messenger.sms.MessageSender
import java.util.concurrent.TimeUnit

class StoryDirectReplyRepository(context: Context) {

  private val context = context.applicationContext

  fun getStoryPost(storyId: Long): Single<MessageRecord> {
    return Single.fromCallable {
      SignalDatabase.messages.getMessageRecord(storyId).withAttachments()
    }.subscribeOn(Schedulers.io())
  }

  fun send(storyId: Long, groupDirectReplyRecipientId: RecipientId?, body: CharSequence, bodyRangeList: BodyRangeList?, isReaction: Boolean): Completable {
    return Completable.create { emitter ->
      val message = SignalDatabase.messages.getMessageRecord(storyId).withAttachments() as MmsMessageRecord
      val (recipient, threadId) = if (groupDirectReplyRecipientId == null) {
        message.fromRecipient to message.threadId
      } else {
        val resolved = Recipient.resolved(groupDirectReplyRecipientId)
        resolved to SignalDatabase.threads.getOrCreateThreadIdFor(resolved)
      }

      val quoteAuthor: Recipient = message.fromRecipient

      MessageSender.send(
        context,
        OutgoingMessage(
          threadRecipient = recipient,
          body = body.toString(),
          sentTimeMillis = System.currentTimeMillis(),
          expiresIn = TimeUnit.SECONDS.toMillis(recipient.expiresInSeconds.toLong()),
          parentStoryId = ParentStoryId.DirectReply(storyId),
          isStoryReaction = isReaction,
          outgoingQuote = QuoteModel(message.dateSent, quoteAuthor.id, message.body, false, message.slideDeck.asAttachments().firstOrNull(), null, QuoteModel.Type.NORMAL, message.messageRanges),
          bodyRanges = bodyRangeList,
          isSecure = true
        ),
        threadId,
        MessageSender.SendType.SIGNAL,
        null
      ) {
        emitter.onComplete()
      }
    }.subscribeOn(Schedulers.io())
  }
}
