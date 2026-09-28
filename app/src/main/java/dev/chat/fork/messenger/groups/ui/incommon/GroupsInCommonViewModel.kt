/*
 * Copyright 2025 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.groups.ui.incommon

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import dev.chat.fork.messenger.groups.GroupsInCommonRepository
import dev.chat.fork.messenger.recipients.Recipient
import dev.chat.fork.messenger.recipients.RecipientId

class GroupsInCommonViewModel(
  context: Context,
  recipientId: RecipientId
) : ViewModel() {

  val groups: StateFlow<List<Recipient>> = GroupsInCommonRepository.getGroupsInCommon(context, recipientId)
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(),
      initialValue = emptyList()
    )
}
