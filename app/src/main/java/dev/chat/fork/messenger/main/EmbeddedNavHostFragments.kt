/*
 * Copyright 2025 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import dev.chat.fork.messenger.MainNavigator
import dev.chat.fork.messenger.R

/**
 * Wrapper fragment that hosts the Settings navigation graph inside the main
 * activity scaffold. This provides the [NavHostFragment] and [androidx.navigation.NavController]
 * that [dev.chat.fork.messenger.components.settings.app.AppSettingsFragment] requires
 * for its internal navigation to sub-settings screens.
 */
class EmbeddedSettingsFragment : Fragment() {

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    return inflater.inflate(R.layout.embedded_nav_host_fragment, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    if (savedInstanceState == null) {
      val navHostFragment = NavHostFragment.create(R.navigation.app_settings_with_change_number)

      childFragmentManager.beginTransaction()
        .replace(R.id.embedded_nav_host_fragment, navHostFragment)
        .commitNow()
    }

    // MainActivity suppresses all drawing until a tab reports its first render (see the
    // OnPreDrawListener in MainActivity.onCreate). The chats/calls/stories tabs report it via
    // their fragments, but when the activity is (re)created directly on the Settings tab —
    // e.g. after a theme change triggers RESULT_CONFIG_CHANGED -> recreate() — nothing would
    // ever unblock drawing, leaving the window black. Report first render once our view is laid out.
    view.post {
      (activity as? MainNavigator.NavigatorProvider)?.onFirstRender()
    }
  }
}

/**
 * Wrapper fragment that hosts the Edit Profile navigation graph inside the main
 * activity scaffold. This provides the [NavHostFragment] and [androidx.navigation.NavController]
 * that [dev.chat.fork.messenger.profiles.manage.EditProfileFragment] requires
 * for its internal navigation to username, avatar, and other profile sub-screens.
 */
class EmbeddedProfileFragment : Fragment() {

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    return inflater.inflate(R.layout.embedded_nav_host_fragment, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    if (savedInstanceState == null) {
      val navHostFragment = NavHostFragment.create(R.navigation.edit_profile)

      childFragmentManager.beginTransaction()
        .replace(R.id.embedded_nav_host_fragment, navHostFragment)
        .commitNow()
    }

    // Same first-render reporting as EmbeddedSettingsFragment: prevents a black screen when
    // MainActivity is (re)created directly on the Profile tab.
    view.post {
      (activity as? MainNavigator.NavigatorProvider)?.onFirstRender()
    }
  }
}
