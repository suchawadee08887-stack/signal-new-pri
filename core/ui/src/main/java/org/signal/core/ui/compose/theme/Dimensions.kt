/*
 * Copyright 2025 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.core.ui.compose.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Shared spacing, sizing, and shape tokens for Compose UI.
 *
 * Prefer these over hard-coded values so shell chrome, lists, and settings
 * stay visually consistent. Values align with the Material 3 4dp grid used
 * across Signal surfaces.
 */
object Dimensions {
  // Spacing (4dp grid)
  val space0: Dp = 0.dp
  val space1: Dp = 4.dp
  val space2: Dp = 8.dp
  val space3: Dp = 12.dp
  val space4: Dp = 16.dp
  val space5: Dp = 20.dp
  val space6: Dp = 24.dp
  val space8: Dp = 32.dp
  val space10: Dp = 40.dp
  val space12: Dp = 48.dp

  /** Default horizontal page gutter for list and settings content. */
  val gutter: Dp = space4

  /** Comfortable horizontal gutter on wider phones. */
  val gutterWide: Dp = space6

  // Corner radii
  val radiusSmall: Dp = 8.dp
  val radiusMedium: Dp = 12.dp
  val radiusLarge: Dp = 18.dp
  val radiusXLarge: Dp = 24.dp
  val radiusFull: Dp = 999.dp

  // Touch / control sizes
  val minTouchTarget: Dp = 48.dp
  val iconButtonSize: Dp = 48.dp
  val iconSize: Dp = 24.dp
  val fabSize: Dp = 56.dp
  val fabSecondarySize: Dp = 56.dp
  val fabSpacing: Dp = space4
  val toolbarAvatarSize: Dp = 28.dp
  val listAvatarSize: Dp = 52.dp

  // Elevation
  val elevationNone: Dp = 0.dp
  val elevationLow: Dp = 2.dp
  val elevationMedium: Dp = 4.dp
  val elevationHigh: Dp = 8.dp

  // Navigation chrome
  val navigationBarHeight: Dp = 80.dp
  val navigationBarHeightCompact: Dp = 56.dp
  val navigationRailItemSpacing: Dp = space4
}
