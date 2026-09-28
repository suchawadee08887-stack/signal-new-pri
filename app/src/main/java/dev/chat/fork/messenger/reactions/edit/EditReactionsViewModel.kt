package dev.chat.fork.messenger.reactions.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import org.signal.core.util.concurrent.SignalExecutors
import dev.chat.fork.messenger.components.emoji.EmojiUtil
import dev.chat.fork.messenger.database.SignalDatabase
import dev.chat.fork.messenger.keyvalue.EmojiValues
import dev.chat.fork.messenger.keyvalue.SignalStore
import dev.chat.fork.messenger.recipients.Recipient
import dev.chat.fork.messenger.storage.StorageSyncHelper
import dev.chat.fork.messenger.util.livedata.LiveDataUtil
import dev.chat.fork.messenger.util.livedata.Store

class EditReactionsViewModel : ViewModel() {

  private val emojiValues: EmojiValues = SignalStore.emoji
  private val store: Store<State> = Store(State(reactions = emojiValues.reactions.map { emojiValues.getPreferredVariation(it) }))

  val reactions: LiveData<List<String>> = LiveDataUtil.mapDistinct(store.stateLiveData, State::reactions)
  val selection: LiveData<Int> = LiveDataUtil.mapDistinct(store.stateLiveData, State::selection)

  fun setSelection(selection: Int) {
    store.update { it.copy(selection = selection) }
  }

  fun onEmojiSelected(emoji: String) {
    store.update { state ->
      if (state.selection != NO_SELECTION && state.selection in state.reactions.indices) {
        emojiValues.setPreferredVariation(emoji)
        val preferredEmoji: String = emojiValues.getPreferredVariation(emoji)
        val newReactions: List<String> = state.reactions.toMutableList().apply { set(state.selection, preferredEmoji) }
        state.copy(reactions = newReactions)
      } else {
        state
      }
    }
  }

  fun resetToDefaults() {
    EmojiValues.DEFAULT_REACTIONS_LIST.forEach { emoji ->
      emojiValues.removePreferredVariation(EmojiUtil.getCanonicalRepresentation(emoji))
    }

    store.update { it.copy(reactions = EmojiValues.DEFAULT_REACTIONS_LIST) }
  }

  fun save() {
    emojiValues.reactions = store.state.reactions

    SignalExecutors.BOUNDED.execute {
      SignalDatabase.recipients.markNeedsSync(Recipient.self().id)
      StorageSyncHelper.scheduleSyncForDataChange()
    }
  }

  companion object {
    const val NO_SELECTION: Int = -1
  }

  data class State(val selection: Int = NO_SELECTION, val reactions: List<String>)
}
