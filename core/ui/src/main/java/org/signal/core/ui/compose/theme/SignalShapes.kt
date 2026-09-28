/*
 * Copyright 2025 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.core.ui.compose.theme

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

/**
 * Comprehensive shape and corner radius token system.
 *
 * Defines all corner radii used across the application.
 * Organized by component context for easy discovery.
 */
@Immutable
data class SignalShapes(
  // Base corner radii
  val radiusNone: CornerBasedShape,
  val radiusTiny: CornerBasedShape,
  val radiusSmall: CornerBasedShape,
  val radiusMedium: CornerBasedShape,
  val radiusLarge: CornerBasedShape,
  val radiusXLarge: CornerBasedShape,
  val radiusFull: CornerBasedShape,

  // Component-specific shapes
  val card: CornerBasedShape,
  val cardElevated: CornerBasedShape,
  val dialog: CornerBasedShape,
  val dialogWide: CornerBasedShape,
  val bottomSheet: CornerBasedShape,
  val bottomSheetTop: CornerBasedShape,
  val bottomSheetRounded: CornerBasedShape,

  // Button shapes
  val buttonSmall: CornerBasedShape,
  val buttonMedium: CornerBasedShape,
  val buttonLarge: CornerBasedShape,
  val buttonFull: CornerBasedShape,
  val buttonTonal: CornerBasedShape,
  val buttonOutlined: CornerBasedShape,
  val buttonDanger: CornerBasedShape,
  val fab: CornerBasedShape,
  val fabSmall: CornerBasedShape,
  val iconButton: CornerBasedShape,

  // Input shapes
  val textField: CornerBasedShape,
  val textFieldFocused: CornerBasedShape,
  val searchField: CornerBasedShape,
  val chip: CornerBasedShape,

  // Message bubble shapes
  val messageBubbleOutgoing: CornerBasedShape,
  val messageBubbleIncoming: CornerBasedShape,
  val messageBubbleOutgoingSingle: CornerBasedShape,
  val messageBubbleIncomingSingle: CornerBasedShape,
  val messageBubbleOutgoingFirst: CornerBasedShape,
  val messageBubbleIncomingFirst: CornerBasedShape,
  val messageBubbleOutgoingLast: CornerBasedShape,
  val messageBubbleIncomingLast: CornerBasedShape,

  // Quote / reply shapes
  val quote: CornerBasedShape,
  val quoteIncoming: CornerBasedShape,
  val quoteOutgoing: CornerBasedShape,

  // Reaction shapes
  val reactionPill: CornerBasedShape,
  val reactionPicker: CornerBasedShape,
  val reactionPickerItem: CornerBasedShape,

  // Avatar shapes
  val avatarCircle: CornerBasedShape,
  val avatarRounded: CornerBasedShape,
  val avatarRoundedLarge: CornerBasedShape,

  // List item shapes
  val listItem: CornerBasedShape,
  val listItemSelected: CornerBasedShape,
  val listItemGroupFirst: CornerBasedShape,
  val listItemGroupLast: CornerBasedShape,

  // Navigation shapes
  val navigationBar: CornerBasedShape,
  val navigationItem: CornerBasedShape,
  val navigationRail: CornerBasedShape,

  // Tab shapes
  val tab: CornerBasedShape,
  val tabSelected: CornerBasedShape,

  // Badge shapes
  val badge: CornerBasedShape,
  val badgeLarge: CornerBasedShape,

  // Progress shapes
  val progressCircular: CornerBasedShape,
  val progressLinear: CornerBasedShape,

  // Snackbar shape
  val snackbar: CornerBasedShape,

  // Tooltip shape
  val tooltip: CornerBasedShape,

  // Image / media shapes
  val image: CornerBasedShape,
  val imageThumbnail: CornerBasedShape,
  val imagePreview: CornerBasedShape,
  val mediaPicker: CornerBasedShape,
  val mediaSelected: CornerBasedShape,
  val cameraPreview: CornerBasedShape,
  val wallpaperPreview: CornerBasedShape,
  val wallpaperChatContainer: CornerBasedShape,

  // QR code shape
  val qrCode: CornerBasedShape,

  // Calendar shape
  val calendar: CornerBasedShape,
  val calendarCell: CornerBasedShape,

  // Story shapes
  val storyPreview: CornerBasedShape,
  val storyRing: CornerBasedShape,

  // Profile shapes
  val profileAvatar: CornerBasedShape,
  val profileSection: CornerBasedShape,

  // Settings shapes
  val settingsSection: CornerBasedShape,
  val settingsItem: CornerBasedShape,
  val settingsPreference: CornerBasedShape,

  // Search shapes
  val searchBar: CornerBasedShape,
  val searchResult: CornerBasedShape,

  // Contact shapes
  val contactCard: CornerBasedShape,
  val contactCheckbox: CornerBasedShape,

  // Attachment shapes
  val attachment: CornerBasedShape,
  val attachmentPreview: CornerBasedShape,

  // Menu / popup shapes
  val menu: CornerBasedShape,
  val popup: CornerBasedShape,

  // Toggle / switch shapes
  val switchTrack: CornerBasedShape,
  val switchThumb: CornerBasedShape,
  val checkbox: CornerBasedShape,
  val radio: CornerBasedShape,

  // Donation / payment
  val donationPill: CornerBasedShape,
  val donationPillStart: CornerBasedShape,
  val donationPillEnd: CornerBasedShape,
  val paymentCard: CornerBasedShape,

  // Selectable
  val selectableIcon: CornerBasedShape,
  val selectableItem: CornerBasedShape,

  // Sheet sections
  val sheetHandle: CornerBasedShape,

  // Overlay shapes
  val overlay: CornerBasedShape,
  val scrim: CornerBasedShape,

  // Frame / container
  val frame: CornerBasedShape,
  val frameRounded: CornerBasedShape,

  // Tooltip shapes
  val tooltipRounded: CornerBasedShape
)

val LocalSignalShapes = staticCompositionLocalOf {
  signalShapes()
}

/** Create the default Signal shapes system. */
internal fun signalShapes(): SignalShapes = SignalShapes(
  // Base corner radii
  radiusNone = RoundedCornerShape(0.dp),
  radiusTiny = RoundedCornerShape(4.dp),
  radiusSmall = RoundedCornerShape(8.dp),
  radiusMedium = RoundedCornerShape(12.dp),
  radiusLarge = RoundedCornerShape(18.dp),
  radiusXLarge = RoundedCornerShape(24.dp),
  radiusFull = RoundedCornerShape(999.dp),

  // Component-specific
  card = RoundedCornerShape(12.dp),
  cardElevated = RoundedCornerShape(16.dp),
  dialog = RoundedCornerShape(28.dp),
  dialogWide = RoundedCornerShape(28.dp),
  bottomSheet = RoundedCornerShape(28.dp),
  bottomSheetTop = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
  bottomSheetRounded = RoundedCornerShape(18.dp),

  // Button shapes
  buttonSmall = RoundedCornerShape(16.dp),
  buttonMedium = RoundedCornerShape(18.dp),
  buttonLarge = RoundedCornerShape(22.dp),
  buttonFull = RoundedCornerShape(999.dp),
  buttonTonal = RoundedCornerShape(18.dp),
  buttonOutlined = RoundedCornerShape(18.dp),
  buttonDanger = RoundedCornerShape(18.dp),
  fab = RoundedCornerShape(18.dp),
  fabSmall = RoundedCornerShape(12.dp),
  iconButton = RoundedCornerShape(999.dp),

  // Input
  textField = RoundedCornerShape(12.dp),
  textFieldFocused = RoundedCornerShape(12.dp),
  searchField = RoundedCornerShape(999.dp),
  chip = RoundedCornerShape(8.dp),

  // Message bubbles
  messageBubbleOutgoing = RoundedCornerShape(18.dp, 18.dp, 4.dp, 18.dp),
  messageBubbleIncoming = RoundedCornerShape(18.dp, 18.dp, 18.dp, 4.dp),
  messageBubbleOutgoingSingle = RoundedCornerShape(18.dp, 18.dp, 4.dp, 18.dp),
  messageBubbleIncomingSingle = RoundedCornerShape(18.dp, 18.dp, 18.dp, 4.dp),
  messageBubbleOutgoingFirst = RoundedCornerShape(18.dp, 18.dp, 4.dp, 18.dp),
  messageBubbleIncomingFirst = RoundedCornerShape(18.dp, 18.dp, 18.dp, 4.dp),
  messageBubbleOutgoingLast = RoundedCornerShape(18.dp, 18.dp, 4.dp, 18.dp),
  messageBubbleIncomingLast = RoundedCornerShape(18.dp, 18.dp, 18.dp, 4.dp),

  // Quote
  quote = RoundedCornerShape(8.dp),
  quoteIncoming = RoundedCornerShape(8.dp),
  quoteOutgoing = RoundedCornerShape(8.dp),

  // Reaction
  reactionPill = RoundedCornerShape(999.dp),
  reactionPicker = RoundedCornerShape(18.dp),
  reactionPickerItem = RoundedCornerShape(999.dp),

  // Avatar
  avatarCircle = RoundedCornerShape(999.dp),
  avatarRounded = RoundedCornerShape(12.dp),
  avatarRoundedLarge = RoundedCornerShape(18.dp),

  // List item
  listItem = RoundedCornerShape(12.dp),
  listItemSelected = RoundedCornerShape(12.dp),
  listItemGroupFirst = RoundedCornerShape(12.dp, 12.dp, 0.dp, 0.dp),
  listItemGroupLast = RoundedCornerShape(0.dp, 0.dp, 12.dp, 12.dp),

  // Navigation
  navigationBar = RoundedCornerShape(0.dp),
  navigationItem = RoundedCornerShape(12.dp),
  navigationRail = RoundedCornerShape(0.dp),

  // Tab
  tab = RoundedCornerShape(0.dp),
  tabSelected = RoundedCornerShape(0.dp),

  // Badge
  badge = RoundedCornerShape(999.dp),
  badgeLarge = RoundedCornerShape(999.dp),

  // Progress
  progressCircular = RoundedCornerShape(999.dp),
  progressLinear = RoundedCornerShape(999.dp),

  // Snackbar
  snackbar = RoundedCornerShape(12.dp),

  // Tooltip
  tooltip = RoundedCornerShape(8.dp),

  // Image / media
  image = RoundedCornerShape(12.dp),
  imageThumbnail = RoundedCornerShape(12.dp),
  imagePreview = RoundedCornerShape(12.dp),
  mediaPicker = RoundedCornerShape(10.dp),
  mediaSelected = RoundedCornerShape(8.dp),
  cameraPreview = RoundedCornerShape(10.dp),
  wallpaperPreview = RoundedCornerShape(12.dp),
  wallpaperChatContainer = RoundedCornerShape(8.dp),

  // QR code
  qrCode = RoundedCornerShape(8.dp),

  // Calendar
  calendar = RoundedCornerShape(8.dp),
  calendarCell = RoundedCornerShape(999.dp),

  // Story
  storyPreview = RoundedCornerShape(10.dp),
  storyRing = RoundedCornerShape(999.dp),

  // Profile
  profileAvatar = RoundedCornerShape(999.dp),
  profileSection = RoundedCornerShape(12.dp),

  // Settings
  settingsSection = RoundedCornerShape(12.dp),
  settingsItem = RoundedCornerShape(12.dp),
  settingsPreference = RoundedCornerShape(12.dp),

  // Search
  searchBar = RoundedCornerShape(999.dp),
  searchResult = RoundedCornerShape(12.dp),

  // Contact
  contactCard = RoundedCornerShape(12.dp),
  contactCheckbox = RoundedCornerShape(999.dp),

  // Attachment
  attachment = RoundedCornerShape(12.dp),
  attachmentPreview = RoundedCornerShape(10.dp),

  // Menu / popup
  menu = RoundedCornerShape(12.dp),
  popup = RoundedCornerShape(8.dp),

  // Toggle
  switchTrack = RoundedCornerShape(999.dp),
  switchThumb = RoundedCornerShape(999.dp),
  checkbox = RoundedCornerShape(4.dp),
  radio = RoundedCornerShape(999.dp),

  // Donation / payment
  donationPill = RoundedCornerShape(12.dp),
  donationPillStart = RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp),
  donationPillEnd = RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp),
  paymentCard = RoundedCornerShape(12.dp),

  // Selectable
  selectableIcon = RoundedCornerShape(999.dp),
  selectableItem = RoundedCornerShape(12.dp),

  // Sheet
  sheetHandle = RoundedCornerShape(999.dp),

  // Overlay
  overlay = RoundedCornerShape(12.dp),
  scrim = RoundedCornerShape(0.dp),

  // Frame
  frame = RoundedCornerShape(0.dp),
  frameRounded = RoundedCornerShape(12.dp),

  // Tooltip
  tooltipRounded = RoundedCornerShape(8.dp)
)