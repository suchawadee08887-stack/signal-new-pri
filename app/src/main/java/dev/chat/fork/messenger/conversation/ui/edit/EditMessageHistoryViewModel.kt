package dev.chat.fork.messenger.conversation.ui.edit

import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import dev.chat.fork.messenger.conversation.ConversationMessage
import dev.chat.fork.messenger.conversation.colors.GroupAuthorNameColorHelper
import dev.chat.fork.messenger.conversation.colors.NameColor
import dev.chat.fork.messenger.recipients.Recipient
import dev.chat.fork.messenger.recipients.RecipientId

/**
 * View model to show history of edits for a specific message.
 */
class EditMessageHistoryViewModel(private val originalMessageId: Long, private val conversationRecipient: Recipient) : ViewModel() {
  private val groupAuthorNameColorHelper = GroupAuthorNameColorHelper()

  fun getEditHistory(): Observable<List<ConversationMessage>> {
    return EditMessageHistoryRepository
      .getEditHistory(originalMessageId)
      .observeOn(AndroidSchedulers.mainThread())
  }

  fun getNameColorsMap(): Observable<Map<RecipientId, NameColor>> {
    return conversationRecipient
      .live()
      .observable()
      .map { recipient ->
        if (recipient.groupId.isPresent) {
          groupAuthorNameColorHelper.getColorMap(recipient.groupId.get())
        } else {
          emptyMap()
        }
      }
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
  }

  fun markRevisionsRead() {
    EditMessageHistoryRepository.markRevisionsRead(originalMessageId)
  }
}
