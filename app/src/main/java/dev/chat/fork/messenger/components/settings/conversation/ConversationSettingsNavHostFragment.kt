/*
 * Copyright 2026 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.components.settings.conversation

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.navigation.fragment.NavHostFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import dev.chat.fork.messenger.R
import dev.chat.fork.messenger.components.settings.DSLSettingsActivity
import dev.chat.fork.messenger.compose.FragmentBackPressedInfo
import dev.chat.fork.messenger.compose.FragmentBackPressedInfoProvider
import dev.chat.fork.messenger.recipients.Recipient
import dev.chat.fork.messenger.recipients.RecipientId

class ConversationSettingsNavHostFragment : NavHostFragment(), FragmentBackPressedInfoProvider {

  companion object {
    suspend fun createArgs(recipientId: RecipientId): Bundle {
      val recipient = withContext(Dispatchers.Default) { Recipient.resolved(recipientId) }

      val args = if (recipient.isGroup) {
        ConversationSettingsFragmentArgs.Builder(null, recipient.requireGroupId(), null)
      } else {
        ConversationSettingsFragmentArgs.Builder(recipientId, null, null)
      }.build()

      return bundleOf(DSLSettingsActivity.ARG_START_BUNDLE to args.toBundle())
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    val args = requireArguments().getBundle(DSLSettingsActivity.ARG_START_BUNDLE)
    navController.setGraph(R.navigation.conversation_settings, args)
    super.onCreate(savedInstanceState)
  }

  override fun getFragmentBackPressedInfo(): Flow<FragmentBackPressedInfo> {
    return navController.currentBackStackEntryFlow.map {
      if (navController.previousBackStackEntry != null) {
        FragmentBackPressedInfo.Enabled { navController.popBackStack() }
      } else {
        FragmentBackPressedInfo.Disabled
      }
    }
  }
}
