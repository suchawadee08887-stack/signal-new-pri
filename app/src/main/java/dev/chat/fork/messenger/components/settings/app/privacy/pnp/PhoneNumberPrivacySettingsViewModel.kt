package dev.chat.fork.messenger.components.settings.app.privacy.pnp

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import dev.chat.fork.messenger.database.SignalDatabase
import dev.chat.fork.messenger.dependencies.AppDependencies
import dev.chat.fork.messenger.jobs.ProfileUploadJob
import dev.chat.fork.messenger.jobs.RefreshAttributesJob
import dev.chat.fork.messenger.jobs.RefreshOwnProfileJob
import dev.chat.fork.messenger.keyvalue.PhoneNumberPrivacyValues.PhoneNumberDiscoverabilityMode
import dev.chat.fork.messenger.keyvalue.PhoneNumberPrivacyValues.PhoneNumberSharingMode
import dev.chat.fork.messenger.keyvalue.SignalStore
import dev.chat.fork.messenger.recipients.Recipient
import dev.chat.fork.messenger.storage.StorageSyncHelper
import kotlin.time.Duration.Companion.seconds

class PhoneNumberPrivacySettingsViewModel : ViewModel() {

  private val _state = mutableStateOf(
    PhoneNumberPrivacySettingsState(
      phoneNumberSharing = SignalStore.phoneNumberPrivacy.isPhoneNumberSharingEnabled,
      discoverableByPhoneNumber = SignalStore.phoneNumberPrivacy.phoneNumberDiscoverabilityMode != PhoneNumberDiscoverabilityMode.NOT_DISCOVERABLE
    )
  )

  val state: State<PhoneNumberPrivacySettingsState> = _state

  init {
    viewModelScope.launch(Dispatchers.Default) {
      while (isActive) {
        refresh()
        delay(5.seconds)
      }
    }
  }

  fun setNobodyCanSeeMyNumber() {
    setPhoneNumberSharingEnabled(false)
  }

  fun setEveryoneCanSeeMyNumber() {
    setPhoneNumberSharingEnabled(true)
    setDiscoverableByPhoneNumber(true)
  }

  fun setNobodyCanFindMeByMyNumber() {
    setDiscoverableByPhoneNumber(false)
  }

  fun setEveryoneCanFindMeByMyNumber() {
    setDiscoverableByPhoneNumber(true)
  }

  private fun setPhoneNumberSharingEnabled(phoneNumberSharingEnabled: Boolean) {
    SignalStore.phoneNumberPrivacy.phoneNumberSharingMode = if (phoneNumberSharingEnabled) PhoneNumberSharingMode.EVERYBODY else PhoneNumberSharingMode.NOBODY
    SignalDatabase.recipients.markNeedsSync(Recipient.self().id)
    StorageSyncHelper.scheduleSyncForDataChange()
    AppDependencies.jobManager.add(ProfileUploadJob())
    refresh()
  }

  private fun setDiscoverableByPhoneNumber(discoverable: Boolean) {
    SignalStore.phoneNumberPrivacy.phoneNumberDiscoverabilityMode = if (discoverable) PhoneNumberDiscoverabilityMode.DISCOVERABLE else PhoneNumberDiscoverabilityMode.NOT_DISCOVERABLE
    SignalDatabase.recipients.markNeedsSync(Recipient.self().id)
    StorageSyncHelper.scheduleSyncForDataChange()
    AppDependencies.jobManager.startChain(RefreshAttributesJob()).then(RefreshOwnProfileJob()).enqueue()
    refresh()
  }

  fun refresh() {
    _state.value = PhoneNumberPrivacySettingsState(
      phoneNumberSharing = SignalStore.phoneNumberPrivacy.isPhoneNumberSharingEnabled,
      discoverableByPhoneNumber = SignalStore.phoneNumberPrivacy.phoneNumberDiscoverabilityMode != PhoneNumberDiscoverabilityMode.NOT_DISCOVERABLE
    )
  }
}
