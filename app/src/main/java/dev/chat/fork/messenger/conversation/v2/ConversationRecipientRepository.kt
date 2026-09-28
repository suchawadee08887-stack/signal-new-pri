package dev.chat.fork.messenger.conversation.v2

import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import dev.chat.fork.messenger.database.SignalDatabase
import dev.chat.fork.messenger.database.model.GroupRecord
import dev.chat.fork.messenger.recipients.Recipient
import java.util.Optional

class ConversationRecipientRepository(threadId: Long) : ViewModel() {

  val conversationRecipient: Observable<Recipient> by lazy {
    val threadRecipientId = Single.fromCallable {
      SignalDatabase.threads.getRecipientIdForThreadId(threadId)!!
    }

    threadRecipientId
      .flatMapObservable { Recipient.observable(it) }
      .subscribeOn(Schedulers.io())
      .observeOn(Schedulers.io())
      .replay(1)
      .refCount()
      .observeOn(Schedulers.io())
  }

  val groupRecord: Observable<Optional<GroupRecord>> by lazy {
    conversationRecipient
      .switchMapSingle {
        Single.fromCallable {
          if (it.isGroup) {
            SignalDatabase.groups.getGroup(it.id)
          } else {
            Optional.empty()
          }
        }
      }
      .replay(1)
      .refCount()
      .observeOn(Schedulers.io())
  }
}
