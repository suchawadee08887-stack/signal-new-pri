/*
 * Copyright 2025 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.main

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.annotation.StringRes
import dev.chat.fork.messenger.R

/**
 * Represents a single navigation destination in the main bottom navigation bar.
 *
 * This is a superset of [MainNavigationListLocation] that adds CONTACTS and SETTINGS
 * as first-class navigation destinations. Each destination maps to an existing
 * [MainNavigationListLocation] when possible, and falls back to an action-based
 * callback for new destinations (CONTACTS, SETTINGS) that use existing Activity intents
 * or fragment transactions.
 */
enum class MainNavigationDestination(
  val id: String,
  @StringRes val labelRes: Int,
  @DrawableRes val iconRes: Int,
  val existingListLocation: MainNavigationListLocation? = null,
  val isNewDestination: Boolean = false
) {
  CHATS(
    id = "chats",
    labelRes = R.string.ConversationListTabs__chats,
    iconRes = R.drawable.symbol_chat_fill_24,
    existingListLocation = MainNavigationListLocation.CHATS
  ),
  CALLS(
    id = "calls",
    labelRes = R.string.ConversationListTabs__calls,
    iconRes = R.drawable.symbol_phone_fill_24,
    existingListLocation = MainNavigationListLocation.CALLS
  ),
  CONTACTS(
    id = "contacts",
    labelRes = R.string.ContactsCursorLoader_contacts,
    iconRes = R.drawable.symbol_person_fill_24,
    isNewDestination = true
  ),
  STORIES(
    id = "stories",
    labelRes = R.string.ConversationListTabs__stories,
    iconRes = R.drawable.symbol_stories_24,
    existingListLocation = MainNavigationListLocation.STORIES
  ),
  SETTINGS(
    id = "settings",
    labelRes = R.string.ConversationListTabs__settings,
    iconRes = R.drawable.ic_settings_24,
    existingListLocation = MainNavigationListLocation.SETTINGS
  ),
  PROFILE(
    id = "profile",
    labelRes = R.string.ConversationListTabs__profile,
    iconRes = R.drawable.symbol_person_fill_24,
    existingListLocation = MainNavigationListLocation.PROFILE
  );

  /** Maps this destination back to [MainNavigationListLocation] if available. */
  fun toListLocationOrNull(): MainNavigationListLocation? = existingListLocation

  companion object {
    private var visibleOrder: List<String> = listOf("chats", "calls", "stories", "settings", "profile")

    /** Returns the currently visible and ordered destinations. */
    fun getVisible(): List<MainNavigationDestination> {
      return visibleOrder.mapNotNull { id -> entries.find { it.id == id } }
    }

    /** Sets the visible destinations in order. Call at runtime to customize the nav bar. */
    fun setVisible(vararg ids: String) {
      visibleOrder = ids.filter { id -> entries.any { it.id == id } }
    }

    /** Resets to the default visible destinations. */
    fun resetToDefault() {
      visibleOrder = listOf("chats", "calls", "stories", "settings", "profile")
    }
  }
}

/**
 * Flexible navigation menu configuration object.
 *
 * Stores the ordered list of visible destinations and any per-item overrides.
 * This is the single source of truth for what appears in the bottom navigation bar.
 *
 * This system allows menu items to be added, removed, reordered, or hidden
 * at runtime without modifying any application code.
 */
data class NavigationMenuConfig(
  val destinations: List<MainNavigationDestination>
) {
  companion object {
    /** Creates the default configuration. */
    fun default(): NavigationMenuConfig {
      return NavigationMenuConfig(
        destinations = MainNavigationDestination.getVisible()
      )
    }

    /** Creates a configuration from a custom ordered list of destination IDs. */
    fun fromIds(vararg ids: String): NavigationMenuConfig {
      MainNavigationDestination.setVisible(*ids)
      return NavigationMenuConfig(
        destinations = MainNavigationDestination.getVisible()
      )
    }
  }
}

/**
 * Provides navigation menu items with their associated data (badge counts, etc.).
 */
data class NavigationMenuItemData(
  val destination: MainNavigationDestination,
  val badgeCount: Int = 0,
  val isSelected: Boolean = false,
  val enabled: Boolean = true
)

/**
 * Manages the navigation menu configuration and provides helpers for the UI layer.
 * Each [MainNavigationDestination] maps to a [MainNavigationListLocation] for existing
 * tabs (CHATS, CALLS, STORIES) or uses a separate callback for new destinations
 * (CONTACTS, SETTINGS).
 */
object NavigationMenuProvider {

  /**
   * Returns the list of active navigation items for the current configuration.
   *
   * @param currentDestination The currently selected [MainNavigationListLocation].
   * @param chatsBadge Unread chat count.
   * @param callsBadge Unseen calls count.
   * @param storiesBadge Unseen stories count.
   * @param isStoriesEnabled Whether the Stories feature is enabled.
   * @param config Optional custom configuration; uses default if null.
   */
  fun getItems(
    currentDestination: MainNavigationListLocation,
    chatsBadge: Int = 0,
    callsBadge: Int = 0,
    storiesBadge: Int = 0,
    isStoriesEnabled: Boolean = true,
    config: NavigationMenuConfig = NavigationMenuConfig.default()
  ): List<NavigationMenuItemData> {
    return config.destinations
      .filter { it != MainNavigationDestination.STORIES || isStoriesEnabled }
      .map { dest ->
        val badge = when (dest) {
          MainNavigationDestination.CHATS -> chatsBadge
          MainNavigationDestination.CALLS -> callsBadge
          MainNavigationDestination.STORIES -> storiesBadge
          else -> 0
        }
        val isSelected = when {
          dest.existingListLocation != null -> dest.existingListLocation == currentDestination
          else -> false
        }
        NavigationMenuItemData(
          destination = dest,
          badgeCount = badge,
          isSelected = isSelected,
          enabled = true
        )
      }
  }

  /** Returns the label string for a destination. */
  fun getLabel(context: Context, destination: MainNavigationDestination): String {
    return context.getString(destination.labelRes)
  }
}