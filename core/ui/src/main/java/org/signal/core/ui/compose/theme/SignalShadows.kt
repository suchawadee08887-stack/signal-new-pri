/*
 * Copyright 2025 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.core.ui.compose.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Comprehensive shadow and elevation token system.
 *
 * Organized by semantic purpose:
 * - **Elevation**: Flat elevation levels (0-5)
 * - **Shadow**: Visual shadow tokens for various components
 * - **Component-specific**: Named shadows for dialogs, FABs, etc.
 */
@Immutable
data class SignalShadows(
  // Elevation levels
  val elevationNone: Dp,
  val elevationFlat: Dp,
  val elevationLow: Dp,
  val elevationMedium: Dp,
  val elevationHigh: Dp,
  val elevationHighest: Dp,

  // Component elevation
  val card: Dp,
  val cardElevated: Dp,
  val dialog: Dp,
  val bottomSheet: Dp,
  val bottomSheetScrim: Dp,
  val fab: Dp,
  val fabElevated: Dp,
  val navigationBar: Dp,
  val navigationRail: Dp,
  val toolbar: Dp,
  val snackbar: Dp,
  val tooltip: Dp,
  val menu: Dp,
  val popup: Dp,
  val chip: Dp,
  val button: Dp,
  val searchBar: Dp,
  val badge: Dp,

  // Bubble elevation
  val messageBubble: Dp,
  val messageBubbleSelected: Dp,

  // Avatar elevation
  val avatar: Dp,
  val avatarOnline: Dp,

  // Progress
  val progress: Dp,

  // Overlay
  val scrim: Dp,
  val overlay: Dp
)

val LocalSignalShadows = staticCompositionLocalOf {
  signalShadows()
}

/** Create the default Signal shadows system. */
internal fun signalShadows(): SignalShadows = SignalShadows(
  // Elevation levels
  elevationNone = 0.dp,
  elevationFlat = 0.dp,
  elevationLow = 2.dp,
  elevationMedium = 4.dp,
  elevationHigh = 8.dp,
  elevationHighest = 16.dp,

  // Component elevation
  card = 0.dp,
  cardElevated = 2.dp,
  dialog = 8.dp,
  bottomSheet = 0.dp,
  bottomSheetScrim = 0.dp,
  fab = 4.dp,
  fabElevated = 8.dp,
  navigationBar = 0.dp,
  navigationRail = 0.dp,
  toolbar = 0.dp,
  snackbar = 4.dp,
  tooltip = 4.dp,
  menu = 4.dp,
  popup = 4.dp,
  chip = 0.dp,
  button = 0.dp,
  searchBar = 2.dp,
  badge = 0.dp,

  // Bubble elevation
  messageBubble = 0.dp,
  messageBubbleSelected = 2.dp,

  // Avatar
  avatar = 0.dp,
  avatarOnline = 0.dp,

  // Progress
  progress = 0.dp,

  // Overlay
  scrim = 0.dp,
  overlay = 0.dp
)