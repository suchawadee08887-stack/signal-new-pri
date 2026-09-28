package dev.chat.fork.messenger.badges.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.reactivex.rxjava3.disposables.CompositeDisposable
import dev.chat.fork.messenger.badges.BadgeRepository
import dev.chat.fork.messenger.badges.models.Badge
import dev.chat.fork.messenger.recipients.Recipient
import dev.chat.fork.messenger.recipients.RecipientId
import dev.chat.fork.messenger.util.livedata.Store

class ViewBadgeViewModel(
  private val startBadge: Badge?,
  private val recipientId: RecipientId,
  private val repository: BadgeRepository
) : ViewModel() {

  private val disposables = CompositeDisposable()

  private val store = Store(ViewBadgeState())

  val state: LiveData<ViewBadgeState> = store.stateLiveData

  init {
    store.update(Recipient.live(recipientId).liveData) { recipient, state ->
      state.copy(
        recipient = recipient,
        allBadgesVisibleOnProfile = recipient.badges,
        selectedBadge = startBadge ?: recipient.badges.firstOrNull(),
        badgeLoadState = ViewBadgeState.LoadState.LOADED
      )
    }
  }

  override fun onCleared() {
    disposables.clear()
  }

  fun onPageSelected(position: Int) {
    if (position > store.state.allBadgesVisibleOnProfile.size - 1 || position < 0) {
      return
    }

    store.update {
      it.copy(selectedBadge = it.allBadgesVisibleOnProfile[position])
    }
  }

  class Factory(
    private val startBadge: Badge?,
    private val recipientId: RecipientId,
    private val repository: BadgeRepository
  ) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      return requireNotNull(modelClass.cast(ViewBadgeViewModel(startBadge, recipientId, repository)))
    }
  }
}
