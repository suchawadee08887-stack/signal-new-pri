/*
 * Copyright 2025 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.core.ui.compose.theme

import androidx.compose.runtime.Composable

/**
 * Centralized theme management object.
 *
 * This object provides a single entry point for all theme tokens in the Signal
 * messaging application. All components should access theme tokens through this
 * object rather than accessing individual token objects directly.
 *
 * Usage:
 * ```kotlin
 * SignalTheme.tokens.colors.surface.background  // Get surface background color
 * SignalTheme.tokens.typography.bodyMedium       // Get body text style
 * SignalTheme.tokens.spacing.space4              // Get 16dp spacing
 * SignalTheme.tokens.shapes.radiusMedium         // Get 12dp corner radius
 * SignalTheme.tokens.motion.durationMedium1      // Get 200ms animation duration
 * SignalTheme.tokens.interactive.buttonDefault   // Get button default state
 * SignalTheme.tokens.components.conversation.toolbarBackground
 * ```
 */
object SignalThemeTokens {
  /** The current color scheme (light or dark). */
  val colors: SignalColorScheme
    @Composable
    get() = LocalSignalColorScheme.current

  /** The current typography system. */
  val typography: SignalTypography
    @Composable
    get() = LocalSignalTypography.current

  /** The current spacing system. */
  val spacing: SignalSpacing
    @Composable
    get() = LocalSignalSpacing.current

  /** The current shape system. */
  val shapes: SignalShapes
    @Composable
    get() = LocalSignalShapes.current

  /** The current motion/animation system. */
  val motion: Motion
    @Composable
    get() = Motion

  /** The current shadow/elevation system. */
  val shadows: SignalShadows
    @Composable
    get() = LocalSignalShadows.current

  /** The current interactive state system. */
  val interactive: SignalInteractiveStates
    @Composable
    get() = LocalSignalInteractiveStates.current

  /** The current component token system. */
  val components: SignalComponentTokens
    @Composable
    get() = LocalSignalComponentTokens.current

  /** Whether the current theme is in dark mode. */
  val isDarkMode: Boolean
    @Composable
    get() = LocalSignalColorScheme.current.isDarkMode
}