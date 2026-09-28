/*
 * Copyright 2026 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.stories.settings

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.navigation.fragment.NavHostFragment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import dev.chat.fork.messenger.R
import dev.chat.fork.messenger.compose.FragmentBackPressedInfo
import dev.chat.fork.messenger.compose.FragmentBackPressedInfoProvider

/**
 * Hosts the story privacy settings nav2 graph within a nav3 detail pane entry.
 */
class StorySettingsNavHostFragment : NavHostFragment(), FragmentBackPressedInfoProvider {

  override fun onCreate(savedInstanceState: Bundle?) {
    navController.setGraph(
      R.navigation.story_privacy_settings,
      bundleOf("title_id" to R.string.StoriesPrivacySettingsFragment__story_privacy)
    )
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
