/*
 * Copyright 2025 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.groups.ui.addmembers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import dev.chat.fork.messenger.contacts.SelectedContact
import dev.chat.fork.messenger.database.SignalDatabase
import dev.chat.fork.messenger.database.model.GroupRecord
import dev.chat.fork.messenger.groups.GroupId
import dev.chat.fork.messenger.groups.SelectionLimits
import dev.chat.fork.messenger.groups.ui.addmembers.AddMembersUiState.UserMessage
import dev.chat.fork.messenger.keyvalue.SignalStore
import dev.chat.fork.messenger.recipients.PhoneNumber
import dev.chat.fork.messenger.recipients.Recipient
import dev.chat.fork.messenger.recipients.RecipientId
import dev.chat.fork.messenger.recipients.RecipientRepository
import dev.chat.fork.messenger.recipients.ui.RecipientSelection
import kotlin.collections.plus

class AddMembersViewModel(
  private val groupId: GroupId,
  existingMembersMinusSelf: Set<RecipientId>,
  selectionLimits: SelectionLimits
) : ViewModel() {

  private val group: GroupRecord = SignalDatabase.groups.requireGroup(groupId)

  private val internalUiState = MutableStateFlow(
    AddMembersUiState(
      existingMembersMinusSelf = existingMembersMinusSelf,
      selectionLimits = selectionLimits
    )
  )
  val uiState: StateFlow<AddMembersUiState> = internalUiState.asStateFlow()

  fun onSearchQueryChanged(query: String) {
    internalUiState.update { it.copy(searchQuery = query) }
  }

  suspend fun shouldAllowSelection(selection: RecipientSelection): Boolean {
    val recipientHasE164 = selection is RecipientSelection.HasId &&
      withContext(Dispatchers.Default) { Recipient.resolved(selection.id) }.hasE164

    return when {
      groupId.isV1 && !recipientHasE164 -> {
        internalUiState.update {
          it.copy(userMessage = UserMessage.CantAddRecipientToLegacyGroup)
        }
        false
      }

      selection is RecipientSelection.HasId -> true
      selection is RecipientSelection.HasPhone -> recipientExists(selection.phone)
      else -> false
    }
  }

  private suspend fun recipientExists(phone: PhoneNumber): Boolean {
    internalUiState.update { it.copy(isLookingUpRecipient = true) }

    return when (val lookupResult = RecipientRepository.lookup(phone)) {
      is RecipientRepository.LookupResult.Success -> {
        internalUiState.update { it.copy(isLookingUpRecipient = false) }
        true
      }

      is RecipientRepository.LookupResult.Failure -> {
        internalUiState.update {
          it.copy(
            isLookingUpRecipient = false,
            userMessage = UserMessage.RecipientLookupFailed(failure = lookupResult)
          )
        }
        false
      }
    }
  }

  fun onSelectionChanged(newSelections: List<SelectedContact>) {
    internalUiState.update {
      it.copy(
        searchQuery = "",
        newSelections = newSelections
      )
    }
  }

  fun addSelectedMembers() {
    viewModelScope.launch {
      val confirmAddMessage = if (uiState.value.newSelections.size == 1) {
        UserMessage.ConfirmAddMember(
          group = group,
          recipient = withContext(Dispatchers.Default) {
            Recipient.resolved(uiState.value.newSelections.single().orCreateRecipientId)
          }
        )
      } else {
        UserMessage.ConfirmAddMembers(
          group = group,
          recipientIds = uiState.value.newSelections.map { it.orCreateRecipientId }.toSet()
        )
      }

      internalUiState.update { it.copy(userMessage = confirmAddMessage) }
    }
  }

  fun selectRecipient(id: RecipientId) {
    internalUiState.update {
      it.copy(pendingRecipientSelections = it.pendingRecipientSelections + id)
    }
  }

  fun clearPendingRecipientSelections() {
    internalUiState.update {
      it.copy(pendingRecipientSelections = emptySet())
    }
  }

  fun clearUserMessage() {
    internalUiState.update { it.copy(userMessage = null) }
  }
}

data class AddMembersUiState(
  val forceSplitPane: Boolean = SignalStore.internal.forceSplitPane,
  val searchQuery: String = "",
  val existingMembersMinusSelf: Set<RecipientId> = emptySet(),
  val selectionLimits: SelectionLimits,
  val newSelections: List<SelectedContact> = emptyList(),
  val isLookingUpRecipient: Boolean = false,
  val pendingRecipientSelections: Set<RecipientId> = emptySet(),
  val userMessage: UserMessage? = null
) {
  val totalMembersCount: Int
    get() = existingMembersMinusSelf.size + newSelections.size + 1

  sealed interface UserMessage {
    data class RecipientLookupFailed(val failure: RecipientRepository.LookupResult.Failure) : UserMessage
    data object CantAddRecipientToLegacyGroup : UserMessage

    sealed interface GroupAddConfirmation : UserMessage {
      val group: GroupRecord
      val recipientIds: Set<RecipientId>
    }

    data class ConfirmAddMember(
      override val group: GroupRecord,
      val recipient: Recipient
    ) : GroupAddConfirmation {
      override val recipientIds: Set<RecipientId> = setOf(recipient.id)
    }

    data class ConfirmAddMembers(
      override val group: GroupRecord,
      override val recipientIds: Set<RecipientId>
    ) : GroupAddConfirmation
  }
}
