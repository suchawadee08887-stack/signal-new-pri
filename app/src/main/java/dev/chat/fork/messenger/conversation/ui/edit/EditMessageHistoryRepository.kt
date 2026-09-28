package dev.chat.fork.messenger.conversation.ui.edit

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.signal.core.util.concurrent.SignalExecutors
import dev.chat.fork.messenger.conversation.ConversationMessage
import dev.chat.fork.messenger.conversation.v2.data.AttachmentHelper
import dev.chat.fork.messenger.database.DatabaseObserver
import dev.chat.fork.messenger.database.SignalDatabase
import dev.chat.fork.messenger.dependencies.AppDependencies
import dev.chat.fork.messenger.notifications.MarkReadReceiver
import dev.chat.fork.messenger.recipients.Recipient

object EditMessageHistoryRepository {

  fun getEditHistory(messageId: Long): Observable<List<ConversationMessage>> {
    return Observable.create { emitter ->
      val threadId: Long = SignalDatabase.messages.getThreadIdForMessage(messageId)
      if (threadId < 0) {
        emitter.onNext(emptyList())
        return@create
      }

      val databaseObserver: DatabaseObserver = AppDependencies.databaseObserver
      val observer = DatabaseObserver.Observer { emitter.onNext(getEditHistorySync(messageId)) }

      databaseObserver.registerConversationObserver(threadId, observer)

      emitter.setCancellable { databaseObserver.unregisterObserver(observer) }
      emitter.onNext(getEditHistorySync(messageId))
    }.subscribeOn(Schedulers.io())
  }

  fun markRevisionsRead(messageId: Long) {
    SignalExecutors.BOUNDED.execute {
      MarkReadReceiver.process(SignalDatabase.messages.setAllEditMessageRevisionsRead(messageId))
    }
  }

  private fun getEditHistorySync(messageId: Long): List<ConversationMessage> {
    val context = AppDependencies.application
    val records = SignalDatabase
      .messages
      .getMessageEditHistory(messageId)
      .toList()
      .reversed()

    if (records.isEmpty()) {
      return emptyList()
    }

    val attachmentHelper = AttachmentHelper()
      .apply {
        addAll(records)
        fetchAttachments()
      }

    val threadRecipient: Recipient = requireNotNull(SignalDatabase.threads.getRecipientForThreadId(records[0].threadId))

    return attachmentHelper
      .buildUpdatedModels(context, records)
      .map { ConversationMessage.ConversationMessageFactory.createWithUnresolvedData(context, it, threadRecipient) }
  }
}
