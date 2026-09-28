/*
 * Copyright 2024 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.banner.banners

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.signal.core.ui.compose.DayNightPreviews
import org.signal.core.ui.compose.Previews
import dev.chat.fork.messenger.R
import dev.chat.fork.messenger.banner.Banner
import dev.chat.fork.messenger.banner.ui.compose.Action
import dev.chat.fork.messenger.banner.ui.compose.DefaultBanner
import dev.chat.fork.messenger.banner.ui.compose.Importance
import dev.chat.fork.messenger.keyvalue.AccountValues
import dev.chat.fork.messenger.keyvalue.AccountValues.UsernameSyncState
import dev.chat.fork.messenger.keyvalue.SignalStore

class UsernameOutOfSyncBanner(private val onActionClick: (UsernameSyncState) -> Unit) : Banner<UsernameSyncState>() {

  override val enabled: Boolean
    get() {
      if (SignalStore.account.isLinkedDevice) {
        return false
      }

      return when (SignalStore.account.usernameSyncState) {
        AccountValues.UsernameSyncState.USERNAME_AND_LINK_CORRUPTED -> true
        AccountValues.UsernameSyncState.LINK_CORRUPTED -> true
        AccountValues.UsernameSyncState.IN_SYNC -> false
      }
    }

  override val dataFlow: Flow<UsernameSyncState>
    get() = flowOf(SignalStore.account.usernameSyncState)

  @Composable
  override fun DisplayBanner(model: UsernameSyncState, contentPadding: PaddingValues) {
    Banner(
      contentPadding = contentPadding,
      usernameSyncState = model,
      onFixClicked = onActionClick
    )
  }
}

@Composable
private fun Banner(contentPadding: PaddingValues, usernameSyncState: UsernameSyncState, onFixClicked: (UsernameSyncState) -> Unit = {}) {
  DefaultBanner(
    title = null,
    body = if (usernameSyncState == UsernameSyncState.USERNAME_AND_LINK_CORRUPTED) {
      stringResource(id = R.string.UsernameOutOfSyncReminder__username_and_link_corrupt)
    } else {
      stringResource(id = R.string.UsernameOutOfSyncReminder__link_corrupt)
    },
    importance = Importance.NORMAL,
    actions = listOf(
      Action(R.string.UsernameOutOfSyncReminder__fix_now) {
        onFixClicked(usernameSyncState)
      }
    ),
    paddingValues = contentPadding
  )
}

@DayNightPreviews
@Composable
private fun BannerPreviewUsernameCorrupted() {
  Previews.Preview {
    Banner(contentPadding = PaddingValues(0.dp), usernameSyncState = UsernameSyncState.USERNAME_AND_LINK_CORRUPTED)
  }
}

@DayNightPreviews
@Composable
private fun BannerPreviewLinkCorrupted() {
  Previews.Preview {
    Banner(contentPadding = PaddingValues(0.dp), usernameSyncState = UsernameSyncState.LINK_CORRUPTED)
  }
}
