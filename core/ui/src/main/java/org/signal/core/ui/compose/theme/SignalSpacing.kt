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
 * Comprehensive spacing, sizing, and layout token system.
 *
 * All values use a 4dp base grid for consistency.
 * Organized into semantic categories for easy discovery.
 */
@Immutable
data class SignalSpacing(
  // Base unit grid (4dp)
  val space0: Dp,
  val space0_5: Dp,
  val space1: Dp,
  val space1_5: Dp,
  val space2: Dp,
  val space2_5: Dp,
  val space3: Dp,
  val space4: Dp,
  val space5: Dp,
  val space6: Dp,
  val space8: Dp,
  val space10: Dp,
  val space12: Dp,
  val space16: Dp,
  val space20: Dp,
  val space24: Dp,
  val space32: Dp,
  val space40: Dp,
  val space48: Dp,

  // Gutter
  val gutter: Dp,
  val gutterWide: Dp,
  val gutterCompact: Dp,

  // Padding - component-level
  val paddingTiny: Dp,
  val paddingSmall: Dp,
  val paddingMedium: Dp,
  val paddingLarge: Dp,
  val paddingExtraLarge: Dp,

  // Padding - message bubble
  val bubblePaddingHorizontal: Dp,
  val bubblePaddingVertical: Dp,
  val bubblePaddingHorizontalCompact: Dp,
  val bubblePaddingVerticalCompact: Dp,

  // Padding - composer
  val composerPaddingHorizontal: Dp,
  val composerPaddingVertical: Dp,

  // Padding - toolbar
  val toolbarPaddingHorizontal: Dp,
  val toolbarPaddingVertical: Dp,

  // Padding - list items
  val listItemPaddingHorizontal: Dp,
  val listItemPaddingVertical: Dp,
  val listItemGap: Dp,

  // Padding - settings
  val settingsItemPaddingHorizontal: Dp,
  val settingsItemPaddingVertical: Dp,
  val settingsSectionGap: Dp,

  // Padding - dialog
  val dialogPaddingHorizontal: Dp,
  val dialogPaddingVertical: Dp,
  val dialogContentGap: Dp,

  // Padding - bottom sheet
  val bottomSheetPaddingHorizontal: Dp,
  val bottomSheetPaddingVertical: Dp,
  val bottomSheetContentGap: Dp,

  // Padding - card
  val cardPaddingHorizontal: Dp,
  val cardPaddingVertical: Dp,
  val cardGap: Dp,

  // Margin
  val marginSmall: Dp,
  val marginMedium: Dp,
  val marginLarge: Dp,
  val marginExtraLarge: Dp,

  // Gap between elements
  val gapSmall: Dp,
  val gapMedium: Dp,
  val gapLarge: Dp,

  // Icon - sizing
  val iconSizeTiny: Dp,
  val iconSizeSmall: Dp,
  val iconSizeMedium: Dp,
  val iconSizeLarge: Dp,
  val iconSizeExtraLarge: Dp,
  val iconSizeJumbo: Dp,
  val iconButtonSize: Dp,
  val iconButtonPadding: Dp,

  // Avatar - sizing
  val avatarSizeTiny: Dp,
  val avatarSizeSmall: Dp,
  val avatarSizeMedium: Dp,
  val avatarSizeLarge: Dp,
  val avatarSizeExtraLarge: Dp,
  val toolbarAvatarSize: Dp,
  val listAvatarSize: Dp,

  // Button - sizing
  val buttonHeightSmall: Dp,
  val buttonHeightMedium: Dp,
  val buttonHeightLarge: Dp,
  val buttonMinWidth: Dp,
  val buttonPaddingHorizontal: Dp,
  val buttonPaddingVertical: Dp,

  // FAB - sizing
  val fabSize: Dp,
  val fabSizeSmall: Dp,
  val fabSecondarySize: Dp,
  val fabSpacing: Dp,

  // Input - sizing
  val inputHeight: Dp,
  val inputMinWidth: Dp,
  val inputPaddingHorizontal: Dp,
  val inputPaddingVertical: Dp,
  val inputBorderWidth: Dp,

  // Touch target
  val minTouchTarget: Dp,
  val touchTargetLarge: Dp,

  // Navigation
  val navigationBarHeight: Dp,
  val navigationBarHeightCompact: Dp,
  val navigationRailItemSpacing: Dp,
  val navigationBarPadding: Dp,
  val navigationBarIconSize: Dp,

  // Toolbar
  val toolbarHeight: Dp,
  val toolbarHeightCompact: Dp,
  val toolbarHeightExpanded: Dp,
  val toolbarIconSize: Dp,

  // Tab
  val tabBarHeight: Dp,
  val tabIconSize: Dp,
  val tabLabelHeight: Dp,

  // Divider
  val dividerThickness: Dp,
  val dividerThicknessMajor: Dp,
  val dividerInset: Dp,

  // Search bar
  val searchBarHeight: Dp,
  val searchBarPadding: Dp,
  val searchIconSize: Dp,

  // Badge
  val badgeSize: Dp,
  val badgeSizeLarge: Dp,
  val badgePadding: Dp,
  val badgeDotSize: Dp,

  // Progress
  val progressIndicatorSize: Dp,
  val progressIndicatorStrokeWidth: Dp,
  val progressBarHeight: Dp,

  // Snackbar
  val snackbarMinWidth: Dp,
  val snackbarMaxWidth: Dp,
  val snackbarPadding: Dp,

  // Tooltip
  val tooltipPadding: Dp,
  val tooltipMinWidth: Dp,

  // Chip
  val chipHeight: Dp,
  val chipPaddingHorizontal: Dp,
  val chipPaddingVertical: Dp,
  val chipIconSize: Dp,
  val chipSpacing: Dp,

  // Reaction pill
  val reactionPillHeight: Dp,
  val reactionPillPadding: Dp,
  val reactionPillIconSize: Dp,
  val reactionPillSpacing: Dp,

  // Thumbnail
  val thumbnailMinWidth: Dp,
  val thumbnailMaxWidth: Dp,
  val thumbnailMinHeight: Dp,
  val thumbnailMaxHeight: Dp,
  val thumbnailGifWidth: Dp,
  val thumbnailRadius: Dp,

  // Audio
  val audioWaveformHeight: Dp,
  val audioSeekBarHeight: Dp,
  val audioPlayPauseSize: Dp,

  // Message-specific
  val messageSpacing: Dp,
  val messageSpacingLarge: Dp,
  val messageCornerSmall: Dp,
  val messageCornerMedium: Dp,
  val messageCornerLarge: Dp,
  val messageCornerFull: Dp,

  // Status indicator
  val statusDotSize: Dp,
  val statusDotSpacing: Dp,
  val deliveryIconSize: Dp,

  // Typing indicator
  val typingIndicatorDotSize: Dp,
  val typingIndicatorSpacing: Dp,
  val typingIndicatorHeight: Dp,

  // Wallpaer
  val wallpaperPreviewCornerRadius: Dp,
  val wallpaperComposePadding: Dp,

  // Media preview
  val mediaPreviewHeight: Dp,
  val mediaPreviewCornerRadius: Dp,
  val mediaPreviewSpacing: Dp,

  // QR code
  val qrCodeSize: Dp,
  val qrCodePadding: Dp,

  // Calendar / Date picker
  val calendarCellSize: Dp,
  val calendarCornerRadius: Dp,

  // Profile
  val profileAvatarSize: Dp,
  val profileNameSpacing: Dp,
  val profileSectionSpacing: Dp
)

val LocalSignalSpacing = staticCompositionLocalOf {
  signalSpacing()
}

/** Create the default Signal spacing system. */
internal fun signalSpacing(): SignalSpacing = SignalSpacing(
  // Base unit grid
  space0 = 0.dp,
  space0_5 = 2.dp,
  space1 = 4.dp,
  space1_5 = 6.dp,
  space2 = 8.dp,
  space2_5 = 10.dp,
  space3 = 12.dp,
  space4 = 16.dp,
  space5 = 20.dp,
  space6 = 24.dp,
  space8 = 32.dp,
  space10 = 40.dp,
  space12 = 48.dp,
  space16 = 64.dp,
  space20 = 80.dp,
  space24 = 96.dp,
  space32 = 128.dp,
  space40 = 160.dp,
  space48 = 192.dp,

  // Gutter
  gutter = 16.dp,
  gutterWide = 24.dp,
  gutterCompact = 12.dp,

  // Padding
  paddingTiny = 4.dp,
  paddingSmall = 8.dp,
  paddingMedium = 16.dp,
  paddingLarge = 24.dp,
  paddingExtraLarge = 32.dp,

  // Bubble
  bubblePaddingHorizontal = 12.dp,
  bubblePaddingVertical = 8.dp,
  bubblePaddingHorizontalCompact = 8.dp,
  bubblePaddingVerticalCompact = 4.dp,

  // Composer
  composerPaddingHorizontal = 8.dp,
  composerPaddingVertical = 4.dp,

  // Toolbar
  toolbarPaddingHorizontal = 16.dp,
  toolbarPaddingVertical = 8.dp,

  // List items
  listItemPaddingHorizontal = 16.dp,
  listItemPaddingVertical = 12.dp,
  listItemGap = 8.dp,

  // Settings
  settingsItemPaddingHorizontal = 16.dp,
  settingsItemPaddingVertical = 16.dp,
  settingsSectionGap = 24.dp,

  // Dialog
  dialogPaddingHorizontal = 24.dp,
  dialogPaddingVertical = 24.dp,
  dialogContentGap = 16.dp,

  // Bottom sheet
  bottomSheetPaddingHorizontal = 24.dp,
  bottomSheetPaddingVertical = 24.dp,
  bottomSheetContentGap = 16.dp,

  // Card
  cardPaddingHorizontal = 16.dp,
  cardPaddingVertical = 16.dp,
  cardGap = 12.dp,

  // Margin
  marginSmall = 8.dp,
  marginMedium = 16.dp,
  marginLarge = 24.dp,
  marginExtraLarge = 32.dp,

  // Gap
  gapSmall = 8.dp,
  gapMedium = 16.dp,
  gapLarge = 24.dp,

  // Icon sizing
  iconSizeTiny = 12.dp,
  iconSizeSmall = 16.dp,
  iconSizeMedium = 20.dp,
  iconSizeLarge = 24.dp,
  iconSizeExtraLarge = 32.dp,
  iconSizeJumbo = 48.dp,
  iconButtonSize = 48.dp,
  iconButtonPadding = 12.dp,

  // Avatar sizing
  avatarSizeTiny = 24.dp,
  avatarSizeSmall = 32.dp,
  avatarSizeMedium = 40.dp,
  avatarSizeLarge = 52.dp,
  avatarSizeExtraLarge = 80.dp,
  toolbarAvatarSize = 28.dp,
  listAvatarSize = 52.dp,

  // Button sizing
  buttonHeightSmall = 32.dp,
  buttonHeightMedium = 40.dp,
  buttonHeightLarge = 44.dp,
  buttonMinWidth = 48.dp,
  buttonPaddingHorizontal = 16.dp,
  buttonPaddingVertical = 10.dp,

  // FAB
  fabSize = 56.dp,
  fabSizeSmall = 40.dp,
  fabSecondarySize = 56.dp,
  fabSpacing = 16.dp,

  // Input
  inputHeight = 48.dp,
  inputMinWidth = 120.dp,
  inputPaddingHorizontal = 16.dp,
  inputPaddingVertical = 12.dp,
  inputBorderWidth = 1.dp,

  // Touch target
  minTouchTarget = 48.dp,
  touchTargetLarge = 56.dp,

  // Navigation
  navigationBarHeight = 80.dp,
  navigationBarHeightCompact = 56.dp,
  navigationRailItemSpacing = 16.dp,
  navigationBarPadding = 4.dp,
  navigationBarIconSize = 24.dp,

  // Toolbar
  toolbarHeight = 56.dp,
  toolbarHeightCompact = 48.dp,
  toolbarHeightExpanded = 64.dp,
  toolbarIconSize = 24.dp,

  // Tab
  tabBarHeight = 48.dp,
  tabIconSize = 24.dp,
  tabLabelHeight = 16.dp,

  // Divider
  dividerThickness = 1.dp,
  dividerThicknessMajor = 2.dp,
  dividerInset = 72.dp,

  // Search bar
  searchBarHeight = 40.dp,
  searchBarPadding = 12.dp,
  searchIconSize = 20.dp,

  // Badge
  badgeSize = 16.dp,
  badgeSizeLarge = 20.dp,
  badgePadding = 4.dp,
  badgeDotSize = 8.dp,

  // Progress
  progressIndicatorSize = 48.dp,
  progressIndicatorStrokeWidth = 4.dp,
  progressBarHeight = 4.dp,

  // Snackbar
  snackbarMinWidth = 288.dp,
  snackbarMaxWidth = 568.dp,
  snackbarPadding = 16.dp,

  // Tooltip
  tooltipPadding = 8.dp,
  tooltipMinWidth = 48.dp,

  // Chip
  chipHeight = 32.dp,
  chipPaddingHorizontal = 8.dp,
  chipPaddingVertical = 6.dp,
  chipIconSize = 20.dp,
  chipSpacing = 8.dp,

  // Reaction pill
  reactionPillHeight = 32.dp,
  reactionPillPadding = 8.dp,
  reactionPillIconSize = 18.dp,
  reactionPillSpacing = 4.dp,

  // Thumbnail
  thumbnailMinWidth = 64.dp,
  thumbnailMaxWidth = 220.dp,
  thumbnailMinHeight = 64.dp,
  thumbnailMaxHeight = 220.dp,
  thumbnailGifWidth = 160.dp,
  thumbnailRadius = 12.dp,

  // Audio
  audioWaveformHeight = 36.dp,
  audioSeekBarHeight = 2.dp,
  audioPlayPauseSize = 36.dp,

  // Message
  messageSpacing = 4.dp,
  messageSpacingLarge = 8.dp,
  messageCornerSmall = 8.dp,
  messageCornerMedium = 12.dp,
  messageCornerLarge = 18.dp,
  messageCornerFull = 999.dp,

  // Status indicator
  statusDotSize = 8.dp,
  statusDotSpacing = 2.dp,
  deliveryIconSize = 14.dp,

  // Typing indicator
  typingIndicatorDotSize = 6.dp,
  typingIndicatorSpacing = 4.dp,
  typingIndicatorHeight = 24.dp,

  // Wallpaper
  wallpaperPreviewCornerRadius = 12.dp,
  wallpaperComposePadding = 8.dp,

  // Media preview
  mediaPreviewHeight = 64.dp,
  mediaPreviewCornerRadius = 10.dp,
  mediaPreviewSpacing = 4.dp,

  // QR code
  qrCodeSize = 180.dp,
  qrCodePadding = 16.dp,

  // Calendar
  calendarCellSize = 40.dp,
  calendarCornerRadius = 8.dp,

  // Profile
  profileAvatarSize = 120.dp,
  profileNameSpacing = 8.dp,
  profileSectionSpacing = 24.dp
)