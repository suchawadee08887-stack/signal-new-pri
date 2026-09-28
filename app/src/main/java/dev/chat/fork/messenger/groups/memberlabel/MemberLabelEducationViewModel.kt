/*
 * Copyright 2026 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.groups.memberlabel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.signal.core.util.concurrent.SignalDispatchers
import dev.chat.fork.messenger.groups.GroupId
import dev.chat.fork.messenger.recipients.Recipient

class MemberLabelEducationViewModel(
  private val groupId: GroupId.V2,
  private val repository: MemberLabelRepository = MemberLabelRepository.instance
) : ViewModel() {

  data class UiState(
    val selfHasLabel: Boolean = false,
    val selfCanSetLabel: Boolean = false
  )

  private val _uiState = MutableStateFlow(UiState())
  val uiState: StateFlow<UiState> = _uiState.asStateFlow()

  init {
    viewModelScope.launch(SignalDispatchers.Default) {
      val self = Recipient.self()
      val selfMemberLabel = repository.getLabel(groupId, self.id)
      val selfCanSetLabel = repository.canSetLabel(groupId, self)
      _uiState.update {
        it.copy(
          selfHasLabel = selfMemberLabel != null,
          selfCanSetLabel = selfCanSetLabel
        )
      }
    }
  }
}
