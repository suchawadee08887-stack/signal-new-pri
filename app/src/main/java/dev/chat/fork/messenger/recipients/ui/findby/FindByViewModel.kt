/*
 * Copyright 2024 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.recipients.ui.findby

import androidx.annotation.WorkerThread
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.signal.core.util.logging.Log
import dev.chat.fork.messenger.profiles.manage.UsernameRepository
import dev.chat.fork.messenger.recipients.PhoneNumber
import dev.chat.fork.messenger.recipients.Recipient
import dev.chat.fork.messenger.recipients.RecipientRepository
import dev.chat.fork.messenger.registration.ui.countrycode.Country
import dev.chat.fork.messenger.util.UsernameUtil

class FindByViewModel(
  mode: FindByMode
) : ViewModel() {

  companion object {
    private val TAG = Log.tag(FindByViewModel::class.java)
  }

  private val internalState = mutableStateOf(
    FindByState.startingState(self = Recipient.self(), mode = mode)
  )

  val state: State<FindByState> = internalState

  fun onUserEntryChanged(userEntry: String) {
    val cleansed = if (state.value.mode == FindByMode.PHONE_NUMBER) {
      userEntry.filter { it.isDigit() }
    } else {
      userEntry
    }

    internalState.value = state.value.copy(userEntry = cleansed)
  }

  fun onCountrySelected(country: Country) {
    internalState.value = state.value.copy(selectedCountry = country)
  }

  suspend fun onNextClicked(): FindByResult {
    internalState.value = state.value.copy(isLookupInProgress = true)
    val findByResult = viewModelScope.async(context = Dispatchers.IO) {
      if (state.value.mode == FindByMode.USERNAME) {
        performUsernameLookup()
      } else {
        performPhoneLookup()
      }
    }.await()

    internalState.value = state.value.copy(
      isLookupInProgress = false,
      lastLookupRecord = formatLookupRecord(findByResult)
    )
    return findByResult
  }

  private fun formatLookupRecord(result: FindByResult): String {
    val mode = state.value.mode.name
    val entry = state.value.userEntry
    return when (result) {
      is FindByResult.Success -> "$mode FOUND entry=$entry recipientId=${result.recipientId}"
      is FindByResult.NotFound -> "$mode NOT_FOUND entry=$entry"
      FindByResult.InvalidEntry -> "$mode INVALID entry=$entry"
      FindByResult.NetworkError -> "$mode NETWORK_ERROR entry=$entry"
    }
  }

  @WorkerThread
  private fun performUsernameLookup(): FindByResult {
    val username = state.value.userEntry.trim()

    if (!UsernameUtil.isValidUsernameForSearch(username)) {
      return FindByResult.InvalidEntry
    }

    return when (val result = UsernameRepository.fetchAciForUsername(usernameString = username.removePrefix("@"))) {
      UsernameRepository.UsernameAciFetchResult.NetworkError -> FindByResult.NetworkError
      UsernameRepository.UsernameAciFetchResult.NotFound -> FindByResult.NotFound()
      is UsernameRepository.UsernameAciFetchResult.Success -> FindByResult.Success(Recipient.externalUsername(result.aci, username).id)
    }
  }

  private suspend fun performPhoneLookup(): FindByResult {
    val stateSnapshot = state.value
    val countryCode = stateSnapshot.selectedCountry.countryCode
    val nationalNumber = stateSnapshot.userEntry.removePrefix(countryCode.toString())

    val e164 = "+$countryCode$nationalNumber"
    Log.d(TAG, "Find-by-phone lookup starting. country=+$countryCode national=$nationalNumber e164=$e164")

    val findByResult = when (val result = RecipientRepository.lookup(PhoneNumber(e164))) {
      is RecipientRepository.PhoneLookupResult.InvalidPhone -> {
        Log.w(TAG, "Find-by-phone record: INVALID e164=$e164 input=${result.invalidValue}")
        FindByResult.InvalidEntry
      }
      is RecipientRepository.PhoneLookupResult.NotFound -> {
        Log.i(TAG, "Find-by-phone record: NOT_FOUND e164=$e164")
        FindByResult.NotFound()
      }
      is RecipientRepository.PhoneLookupResult.Found -> {
        Log.i(TAG, "Find-by-phone record: FOUND e164=$e164 recipientId=${result.recipient.id}")
        FindByResult.Success(result.recipient.id)
      }
      is RecipientRepository.LookupResult.NetworkError -> {
        Log.w(TAG, "Find-by-phone record: NETWORK_ERROR e164=$e164")
        FindByResult.NetworkError
      }
    }
    return findByResult
  }

  fun filterCountries(filterBy: String) {
    if (filterBy.isEmpty()) {
      internalState.value = state.value.copy(
        query = filterBy,
        filteredCountries = emptyList()
      )
    } else {
      internalState.value = state.value.copy(
        query = filterBy,
        filteredCountries = state.value.supportedCountries.filter { country: Country ->
          country.name.contains(filterBy, ignoreCase = true) ||
            country.countryCode.toString().contains(filterBy.removePrefix("+")) ||
            (filterBy.equals("usa", ignoreCase = true) && country.name.equals("United States", ignoreCase = true))
        }
      )
    }
  }
}
