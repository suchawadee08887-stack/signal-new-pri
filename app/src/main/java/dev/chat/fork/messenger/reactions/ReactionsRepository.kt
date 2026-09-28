package dev.chat.fork.messenger.reactions

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableEmitter
import io.reactivex.rxjava3.schedulers.Schedulers
import org.signal.core.util.concurrent.SignalExecutors
import dev.chat.fork.messenger.components.emoji.EmojiUtil
import dev.chat.fork.messenger.database.DatabaseObserver
import dev.chat.fork.messenger.database.SignalDatabase
import dev.chat.fork.messenger.database.model.MessageId
import dev.chat.fork.messenger.database.model.ReactionRecord
import dev.chat.fork.messenger.dependencies.AppDependencies
import dev.chat.fork.messenger.recipients.Recipient
import dev.chat.fork.messenger.sms.MessageSender

class ReactionsRepository {

  fun getReactions(messageId: MessageId): Observable<List<ReactionDetails>> {
    return Observable.create { emitter: ObservableEmitter<List<ReactionDetails>> ->
      val databaseObserver: DatabaseObserver = AppDependencies.databaseObserver

      val messageObserver = DatabaseObserver.MessageObserver { reactionMessageId ->
        if (reactionMessageId == messageId) {
          emitter.onNext(fetchReactionDetails(reactionMessageId))
        }
      }

      databaseObserver.registerMessageUpdateObserver(messageObserver)

      emitter.setCancellable {
        databaseObserver.unregisterObserver(messageObserver)
      }

      emitter.onNext(fetchReactionDetails(messageId))
    }.subscribeOn(Schedulers.io())
  }

  private fun fetchReactionDetails(messageId: MessageId): List<ReactionDetails> {
    val reactions: List<ReactionRecord> = SignalDatabase.reactions.getReactions(messageId)

    return reactions.map { reaction ->
      ReactionDetails(
        sender = Recipient.resolved(reaction.author),
        baseEmoji = EmojiUtil.getCanonicalRepresentation(reaction.emoji),
        displayEmoji = reaction.emoji,
        timestamp = reaction.dateReceived
      )
    }
  }

  fun sendReactionRemoval(messageId: MessageId) {
    val oldReactionRecord = SignalDatabase.reactions.getReactions(messageId).firstOrNull { it.author == Recipient.self().id } ?: return
    SignalExecutors.BOUNDED.execute {
      MessageSender.sendReactionRemoval(
        AppDependencies.application.applicationContext,
        MessageId(messageId.id),
        oldReactionRecord
      )
    }
  }
}
