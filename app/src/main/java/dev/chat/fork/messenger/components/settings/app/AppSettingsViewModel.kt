package dev.chat.fork.messenger.components.settings.app

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.plusAssign
import io.reactivex.rxjava3.kotlin.subscribeBy
import dev.chat.fork.messenger.components.settings.app.subscription.InAppDonations
import dev.chat.fork.messenger.components.settings.app.subscription.RecurringInAppPaymentRepository
import dev.chat.fork.messenger.conversationlist.model.UnreadPaymentsLiveData
import dev.chat.fork.messenger.database.model.InAppPaymentSubscriberRecord
import dev.chat.fork.messenger.dependencies.AppDependencies
import dev.chat.fork.messenger.keyvalue.SignalStore
import dev.chat.fork.messenger.recipients.Recipient
import dev.chat.fork.messenger.util.TextSecurePreferences
import dev.chat.fork.messenger.util.livedata.Store

class AppSettingsViewModel : ViewModel() {

  private val store = Store(
    AppSettingsState(
      isPrimaryDevice = SignalStore.account.isPrimaryDevice,
      unreadPaymentsCount = 0,
      hasExpiredGiftBadge = SignalStore.inAppPayments.getExpiredGiftBadge() != null,
      allowUserToGoToDonationManagementScreen = SignalStore.inAppPayments.isLikelyASustainer() || InAppDonations.hasAtLeastOnePaymentMethodAvailable(),
      userUnregistered = TextSecurePreferences.isUnauthorizedReceived(AppDependencies.application) || !SignalStore.account.isRegistered,
      clientDeprecated = SignalStore.misc.isClientDeprecated
    )
  )

  private val unreadPaymentsLiveData = UnreadPaymentsLiveData()
  private val disposables = CompositeDisposable()

  val state: LiveData<AppSettingsState> = store.stateLiveData
  val self: LiveData<BioRecipientState> = Recipient.self().live().liveData.map { BioRecipientState(it) }

  init {
    store.update(unreadPaymentsLiveData) { payments, state -> state.copy(unreadPaymentsCount = payments.map { it.unreadCount }.orElse(0)) }

    disposables += RecurringInAppPaymentRepository.getActiveSubscription(InAppPaymentSubscriberRecord.Type.DONATION).subscribeBy(
      onSuccess = { activeSubscription ->
        store.update { state ->
          state.copy(allowUserToGoToDonationManagementScreen = SignalStore.account.isRegistered && (activeSubscription.isActive || InAppDonations.hasAtLeastOnePaymentMethodAvailable()))
        }
      },
      onError = {}
    )
  }

  override fun onCleared() {
    disposables.clear()
  }

  fun refreshDeprecatedOrUnregistered() {
    store.update {
      it.copy(
        clientDeprecated = SignalStore.misc.isClientDeprecated,
        userUnregistered = TextSecurePreferences.isUnauthorizedReceived(AppDependencies.application) || !SignalStore.account.isRegistered
      )
    }
  }

  fun refresh() {
    store.update {
      it.copy(
        hasExpiredGiftBadge = SignalStore.inAppPayments.getExpiredGiftBadge() != null,
        backupFailureState = getBackupFailureState()
      )
    }
  }

  private fun getBackupFailureState(): BackupFailureState {
    return when {
      !SignalStore.account.isRegistered || !SignalStore.backup.areBackupsEnabled -> BackupFailureState.NONE
      SignalStore.backup.isNotEnoughRemoteStorageSpace -> BackupFailureState.OUT_OF_STORAGE_SPACE
      SignalStore.backup.hasBackupCreationError -> BackupFailureState.COULD_NOT_COMPLETE_BACKUP
      SignalStore.backup.subscriptionStateMismatchDetected -> BackupFailureState.SUBSCRIPTION_STATE_MISMATCH
      SignalStore.backup.hasBackupAlreadyRedeemedError -> BackupFailureState.ALREADY_REDEEMED
      else -> BackupFailureState.NONE
    }
  }
}
