/*
 * Copyright 2025 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.theme

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import dev.chat.fork.messenger.R
import dev.chat.fork.messenger.keyvalue.SettingsValues
import dev.chat.fork.messenger.keyvalue.SignalStore
import dev.chat.fork.messenger.util.CachedInflater
import org.signal.core.util.ConfigurationUtil
import org.signal.core.util.logging.Log

/**
 * Centralized ThemeManager for the Signal messaging application.
 *
 * Bridges the Compose theme system (core/ui) with the Android XML theme system (app) and the
 * persisted user preference store (SignalStore/SettingsValues).
 *
 * Features:
 * - Reads/writes the user's light/dark/system preference from SignalStore
 * - Applies the theme to AppCompatDelegate for both XML and Compose UIs
 * - Provides Compose-friendly helpers to resolve the effective dark mode
 * - Integrates with Activity/Fragment lifecycle via onCreate()/onResume()
 * - Provides utility methods for XML theme resource IDs and theme labels
 *
 * Usage:
 * - In Activities: `ThemeManager.current.onCreate(this)` in onCreate()
 * - In Composables: `val isDark = ThemeManager.resolveDarkMode()`
 * - For theme switching: `ThemeManager.setUserTheme(context, Theme.DARK)`
 */
class ThemeManager private constructor() {

    private var onCreateNightModeConfiguration: Int = Configuration.UI_MODE_NIGHT_UNDEFINED

    companion object {
        private val TAG = Log.tag(ThemeManager::class.java)
        private var globalNightModeConfiguration: Int = Configuration.UI_MODE_NIGHT_UNDEFINED

        @JvmStatic
        val current: ThemeManager by lazy { ThemeManager() }

        // ------------------------------------------------------------------
        // Preference read/write
        // ------------------------------------------------------------------

        @JvmStatic
        fun getUserThemePreference(): SettingsValues.Theme = SignalStore.settings.theme

        @JvmStatic
        fun setUserTheme(context: Context, theme: SettingsValues.Theme) {
            SignalStore.settings.setTheme(theme)
            applyThemePreference(context)
        }

        // ------------------------------------------------------------------
        // Night-mode application
        // ------------------------------------------------------------------

        @JvmStatic
        fun applyThemePreference(context: Context) {
            when (getUserThemePreference()) {
                SettingsValues.Theme.SYSTEM -> {
                    Log.d(TAG, "Setting to follow system")
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }
                SettingsValues.Theme.LIGHT -> {
                    Log.d(TAG, "Setting to always day")
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
                SettingsValues.Theme.DARK -> {
                    Log.d(TAG, "Setting to always night")
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
            }
            CachedInflater.from(context).clear()
        }

        // ------------------------------------------------------------------
        // Dark-mode queries
        // ------------------------------------------------------------------

        @JvmStatic
        fun isDarkTheme(context: Context): Boolean = when (getUserThemePreference()) {
            SettingsValues.Theme.SYSTEM -> isSystemInDarkTheme(context)
            SettingsValues.Theme.DARK -> true
            SettingsValues.Theme.LIGHT -> false
        }

        @JvmStatic
        fun isSystemInDarkTheme(context: Context): Boolean =
            (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
                Configuration.UI_MODE_NIGHT_YES

        // ------------------------------------------------------------------
        // XML resource helpers
        // ------------------------------------------------------------------

        @JvmStatic @StyleRes
        fun getConversationThemeResId(isDark: Boolean = false, wallpaper: Boolean = false): Int = when {
            wallpaper -> R.style.TextSecure_DarkTheme_Conversation
            isDark -> R.style.TextSecure_DarkTheme_Conversation
            else -> R.style.TextSecure_LightTheme_Conversation
        }

        @JvmStatic @StyleRes
        fun getNoActionBarThemeResId(): Int = R.style.Signal_DayNight_NoActionBar

        // ------------------------------------------------------------------
        // Compose helpers
        // ------------------------------------------------------------------

        @Composable
        fun resolveDarkMode(): Boolean {
            val configuration = LocalConfiguration.current
            return when (getUserThemePreference()) {
                SettingsValues.Theme.SYSTEM -> {
                    (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
                        Configuration.UI_MODE_NIGHT_YES
                }
                SettingsValues.Theme.DARK -> true
                SettingsValues.Theme.LIGHT -> false
            }
        }

        @JvmStatic
        fun isSystemThemeAvailable(): Boolean = android.os.Build.VERSION.SDK_INT >= 29

        @JvmStatic
        fun getThemeLabel(context: Context, theme: SettingsValues.Theme): String = when (theme) {
            SettingsValues.Theme.SYSTEM -> context.getString(R.string.preferences__system_default)
            SettingsValues.Theme.LIGHT -> context.getString(R.string.preferences__light_theme)
            SettingsValues.Theme.DARK -> context.getString(R.string.preferences__dark_theme)
        }
    }

    // ------------------------------------------------------------------
    // Activity lifecycle
    // ------------------------------------------------------------------

    fun onCreate(activity: Activity) {
        val previousGlobalConfiguration = globalNightModeConfiguration

        onCreateNightModeConfiguration = ConfigurationUtil.getNightModeConfiguration(activity)
        globalNightModeConfiguration = onCreateNightModeConfiguration

        activity.setTheme(getTheme())

        if (previousGlobalConfiguration != globalNightModeConfiguration) {
            Log.d(TAG, "Previous night mode has changed, clearing cached inflater")
            CachedInflater.from(activity).clear()
        }
    }

    fun onResume(activity: Activity) {
        if (onCreateNightModeConfiguration != ConfigurationUtil.getNightModeConfiguration(activity)) {
            Log.d(TAG, "Create configuration different from current, clearing cached inflater")
            CachedInflater.from(activity).clear()
        }
    }

    @StyleRes
    protected open fun getTheme(): Int = R.style.Signal_DayNight
}

/**
 * Activity base class that automatically manages theme configuration.
 * Calls [ThemeManager.onCreate] and [ThemeManager.onResume] at the appropriate lifecycle points.
 */
abstract class ThemedActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.current.onCreate(this)
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        ThemeManager.current.onResume(this)
    }
}

/**
 * Composable extension that returns true when the user's current theme preference
 * resolves to dark mode at the call site.
 */
@Composable
fun isUserDarkTheme(): Boolean = ThemeManager.resolveDarkMode()