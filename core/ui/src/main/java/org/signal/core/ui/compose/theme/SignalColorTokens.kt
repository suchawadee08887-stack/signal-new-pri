/*
 * Copyright 2025 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.core.ui.compose.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Comprehensive color token system for the Signal messaging application.
 *
 * Organized into semantic categories:
 * - **Brand**: Primary accent, brand identity colors
 * - **Surface**: Background layers (1-5 for elevation stacking)
 * - **On-Surface**: Text and icons on surface backgrounds
 * - **Interactive**: States for buttons, controls, and interactive elements
 * - **Message**: Conversation-specific colors (bubbles, status, indicators)
 * - **Status**: Online/offline, read/unread, delivery states
 * - **Component**: Colors for specific UI components (compose bar, toolbar, FAB, etc.)
 * - **Transparent**: Pre-computed transparency levels for overlays
 * - **Inverse**: Colors for content on colored backgrounds
 *
 * All colors are defined as immutable data and provided via CompositionLocal.
 * Use [SignalColorScheme] to access light or dark scheme based on current theme mode.
 */

// ============================================================================
// Brand & Accent Colors
// ============================================================================

@Immutable
data class BrandColors(
  val primary: Color,
  val primaryLight: Color,
  val primaryDark: Color,
  val primaryContainer: Color,
  val onPrimary: Color,
  val onPrimaryContainer: Color,
  val secondary: Color,
  val secondaryContainer: Color,
  val onSecondary: Color,
  val onSecondaryContainer: Color,
  val accent: Color,
  val accentLight: Color,
  val accentDark: Color
)

// ============================================================================
// Surface & Background Colors
// ============================================================================

@Immutable
data class SurfaceColors(
  val background: Color,
  val surface: Color,
  val surface1: Color,
  val surface2: Color,
  val surface3: Color,
  val surface4: Color,
  val surface5: Color,
  val surfaceVariant: Color,
  val surfaceHighest: Color,
  val surfaceLowest: Color,
  val dialog: Color,
  val dialogSecondary: Color,
  val card: Color,
  val cardElevated: Color,
  val sheet: Color,
  val sheetScrim: Color,
  val neutralSurface: Color
)

// ============================================================================
// Text & Icon Colors
// ============================================================================

@Immutable
data class TextColors(
  val primary: Color,
  val primaryDisabled: Color,
  val primaryDialog: Color,
  val secondary: Color,
  val secondaryDisabled: Color,
  val tertiary: Color,
  val hint: Color,
  val inverse: Color,
  val onPrimary: Color,
  val onSecondary: Color,
  val onSurface: Color,
  val onSurfaceVariant: Color,
  val onBackground: Color,
  val link: Color,
  val destructive: Color
)

@Immutable
data class IconColors(
  val primary: Color,
  val secondary: Color,
  val action: Color,
  val onPrimary: Color,
  val onSecondary: Color,
  val tabSelected: Color,
  val tabUnselected: Color,
  val destructive: Color
)

// ============================================================================
// Interactive State Colors
// ============================================================================

@Immutable
data class InteractiveStateColors(
  // Button states
  val buttonPrimary: Color,
  val buttonPrimaryPressed: Color,
  val buttonPrimaryDisabled: Color,
  val buttonPrimaryText: Color,
  val buttonPrimaryTextDisabled: Color,
  val buttonPrimaryRipple: Color,
  val buttonSecondary: Color,
  val buttonSecondaryPressed: Color,
  val buttonSecondaryDisabled: Color,
  val buttonSecondaryText: Color,
  val buttonSecondaryTextDisabled: Color,
  val buttonSecondaryStroke: Color,
  val buttonSecondaryRipple: Color,
  val buttonTonal: Color,
  val buttonDanger: Color,
  val buttonDangerText: Color,

  // Selection & focus
  val selection: Color,
  val selectionDisabled: Color,
  val focusRing: Color,
  val hover: Color,
  val hoverOverlay: Color,
  val pressed: Color,
  val pressedOverlay: Color,
  val disabled: Color,
  val disabledOverlay: Color,
  val selected: Color,
  val selectedOverlay: Color,

  // Touch / ripple
  val touchHighlight: Color,
  val ripple: Color,
  val rippleInverse: Color,

  // Form controls
  val checkbox: Color,
  val checkboxDisabled: Color,
  val switch: Color,
  val switchDisabled: Color,
  val radio: Color,
  val radioDisabled: Color,
  val textCursor: Color,
  val textHighlight: Color
)

// ============================================================================
// Message / Conversation Colors
// ============================================================================

@Immutable
data class MessageColors(
  // Bubble colors
  val outgoingBubble: Color,
  val incomingBubble: Color,
  val outgoingBubbleWallpaper: Color,
  val incomingBubbleWallpaper: Color,
  val noBubble: Color,

  // Text in bubbles
  val outgoingText: Color,
  val outgoingTextSecondary: Color,
  val incomingText: Color,
  val incomingTextSecondary: Color,
  val outgoingFooter: Color,
  val incomingFooter: Color,
  val updateText: Color,
  val linkText: Color,

  // Delivery status
  val sentIcon: Color,
  val deliveredIcon: Color,
  val readIcon: Color,
  val errorIcon: Color,

  // Message states
  val messagePending: Color,
  val messageSending: Color,
  val messageSent: Color,
  val messageDelivered: Color,
  val messageRead: Color,
  val messageFailed: Color,
  val messageExpiring: Color,

  // Read/Unread indicators
  val unreadIndicator: Color,
  val readIndicator: Color,
  val unreadDot: Color,

  // Typing indicator
  val typingForeground: Color,
  val typingForegroundWallpaper: Color,

  // Scroll indicator
  val scrollToBottom: Color,

  // Selection
  val selectedMessage: Color,
  val selectedMessageOverlay: Color,

  // Quote / reply
  val quoteBackgroundIncoming: Color,
  val quoteBackgroundOutgoing: Color,
  val quoteBarIncoming: Color,
  val quoteBarOutgoing: Color,
  val quoteTextIncoming: Color,
  val quoteTextOutgoing: Color,
  val quoteLabelBackgroundIncoming: Color,
  val quoteLabelBackgroundOutgoing: Color,
  val quoteMissingIcon: Color,

  // Reactions
  val reactionPill: Color,
  val reactionPillSelected: Color,
  val reactionPillText: Color,
  val reactionPillSelectedText: Color,
  val reactionOverlay: Color,

  // Media
  val mediaThumbnailOverlay: Color,
  val mediaThumbCircle: Color,
  val audioForegroundOutgoing: Color,
  val audioForegroundIncoming: Color,
  val audioForegroundIncomingWallpaper: Color,
  val audioPlayPauseBackgroundOutgoing: Color,
  val audioPlayPauseBackgroundIncoming: Color,
  val audioPlayPauseBackgroundIncomingWallpaper: Color,
  val audioSeekBarPlayed: Color,
  val audioSeekBarUnplayed: Color,

  // View once
  val viewOnceCircleIncoming: Color,
  val viewOnceCircleIncomingWallpaper: Color,
  val viewOnceUnopenedIncoming: Color,
  val viewOnceOpenedIncoming: Color,
  val viewOnceCircleOutgoing: Color,
  val viewOnceCircleOutgoingWallpaper: Color,
  val viewOnceUnopenedOutgoing: Color,
  val viewOnceOpenedOutgoing: Color,

  // Mention
  val mentionBackground: Color,

  // Pulse
  val pulseIncoming: Color,
  val pulseOutgoing: Color,

  // Message request bar
  val messageRequestContainer: Color,
  val messageRequestBackground: Color,
  val messageRequestDeny: Color,
  val messageRequestAccept: Color,
  val messageRequestContainerWallpaper: Color,
  val messageRequestBackgroundWallpaper: Color,
  val messageRequestDenyWallpaper: Color,
  val messageRequestAcceptWallpaper: Color,

  // Safety tip
  val safetyTipBackground: Color,
  val safetyTipImageBackground: Color,

  // Gift / sticker
  val sentGiftTint: Color,
  val recvGiftTint: Color,

  // Sticker management
  val stickerManagementAction: Color
)

// ============================================================================
// Status & Indicator Colors
// ============================================================================

@Immutable
data class StatusColors(
  // Online / offline
  val online: Color,
  val offline: Color,
  val away: Color,
  val busy: Color,

  // Delivery status
  val sent: Color,
  val delivered: Color,
  val read: Color,
  val failed: Color,
  val pending: Color,

  // Verification
  val verified: Color,
  val unverified: Color,
  val safetyNumber: Color,

  // Warning & error
  val error: Color,
  val errorContainer: Color,
  val warning: Color,
  val warningOn: Color,
  val warningContainer: Color,
  val success: Color,
  val successContainer: Color,
  val info: Color,
  val infoContainer: Color,

  // Unread badge
  val unreadBadge: Color,
  val unreadBadgeText: Color
)

// ============================================================================
// Divider & Border Colors
// ============================================================================

@Immutable
data class BorderColors(
  val dividerMinor: Color,
  val dividerMajor: Color,
  val dividerInset: Color,
  val border: Color,
  val borderFocus: Color,
  val borderError: Color,
  val borderWarning: Color,
  val outline: Color,
  val outlineVariant: Color
)

// ============================================================================
// Component-Specific Colors
// ============================================================================

@Immutable
data class ComponentColors(
  // Toolbar / App Bar
  val toolbarBackground: Color,
  val toolbarBackgroundScrolled: Color,
  val toolbarBackgroundWallpaper: Color,
  val toolbarBackgroundWallpaperScrolled: Color,
  val toolbarBackgroundIncognito: Color,
  val toolbarForeground: Color,
  val toolbarSubtitle: Color,

  // FAB
  val fabBackground: Color,
  val fabForeground: Color,
  val fabSecondaryBackground: Color,
  val fabSecondaryForeground: Color,

  // Search
  val searchBackground: Color,
  val searchHint: Color,
  val searchIcon: Color,

  // Conversation list
  val conversationListSelected: Color,
  val conversationListArchiveStart: Color,
  val conversationListArchiveEnd: Color,
  val conversationListUnread: Color,

  // Navigation bar
  val navigationBar: Color,
  val navigationBarScrim: Color,
  val navigationItemActive: Color,
  val navigationItemInactive: Color,

  // Bottom sheet
  val bottomSheetBackground: Color,
  val bottomSheetScrim: Color,
  val bottomSheetNavigationBar: Color,

  // Snackbar
  val snackbarBackground: Color,
  val snackbarForeground: Color,
  val snackbarAction: Color,

  // Toast
  val toastBackground: Color,
  val toastForeground: Color,

  // Tooltip
  val tooltipBackground: Color,
  val tooltipForeground: Color,

  // Compose bar / composer
  val composerBackground: Color,
  val composerDivider: Color,
  val composerText: Color,
  val composerHint: Color,
  val composerAttachButton: Color,
  val composerSendButton: Color,
  val composerMediaKeyboardBg: Color,
  val composerMediaKeyboardBarBg: Color,

  // Media preview / overview
  val mediaPreviewBarBackground: Color,
  val mediaOverviewToolbarBackground: Color,
  val mediaOverviewToolbarSecondary: Color,
  val mediaOverviewToolbarForeground: Color,
  val mediaOverviewHeaderForeground: Color,

  // Camera
  val cameraIconBackground: Color,

  // Contacts
  val contactCheckboxBackground: Color,
  val contactFilterToolbarIcon: Color,

  // Wallpaper
  val wallpaperPreviewBackground: Color,
  val wallpaperComposeBackground: Color,
  val wallpaperBubble: Color,
  val wallpaperNavigation: Color,

  // Story
  val storyBullet: Color,
  val storyGradientStart: Color,
  val storyCaptionGradientStart: Color,

  // Donation / payment
  val paymentCardOverflow: Color,
  val paymentCurrencyCode: Color,

  // Insights / analytics
  val insightTitle: Color,
  val insightBody: Color,
  val insightProgressBackground: Color,

  // QR code
  val qrCardColor: Color,
  val qrCardTextColor: Color,

  // Release notes
  val releaseNotesBubble: Color,
  val releaseNotesBubbleText: Color,
  val releaseNotesCtaBackground: Color,
  val releaseNotesBackgroundPattern: Color,
  val releaseNotesHeaderBorder: Color,
  val releaseNotesBackground: Color,
  val releaseNotesHeaderBackground: Color,
  val releaseNotesToolbarScrolled: Color,
  val releaseNotesToolbarTransparent: Color,

  // Megaphone / banner
  val megaphoneBackground: Color,
  val megaphoneBodyText: Color,

  // Voice note player
  val voiceNotePlayerBackground: Color,

  // Numpad
  val numPadKeyBackground: Color,

  // Attachment keyboard
  val attachmentKeyboardForeground: Color,

  // Login
  val loginTopBackground: Color,

  // Group settings
  val groupStoryIndicator: Color,

  // React with any
  val reactWithAnyBackground: Color,
  val reactWithAnySearchBackground: Color,
  val reactWithAnySearchHint: Color,
  val reactWithAnyCustomizeBackground: Color,

  // Settings
  val settingsRipple: Color,

  // Low priority
  val lowPriorityButtonText: Color
)

// ============================================================================
// Transparency / Overlay Colors
// ============================================================================

@Immutable
data class TransparencyColors(
  // Standard transparency
  val transparent: Color,
  val overlay5: Color,
  val overlay10: Color,
  val overlay15: Color,
  val overlay20: Color,
  val overlay25: Color,
  val overlay30: Color,
  val overlay40: Color,
  val overlay50: Color,
  val overlay60: Color,
  val overlay70: Color,
  val overlay80: Color,

  // Inverse transparency (for colored backgrounds)
  val inverseTransparent: Color,
  val inverseOverlay5: Color,
  val inverseOverlay10: Color,
  val inverseOverlay15: Color,
  val inverseOverlay20: Color,
  val inverseOverlay25: Color,
  val inverseOverlay30: Color,
  val inverseOverlay40: Color,
  val inverseOverlay50: Color,
  val inverseOverlay60: Color,
  val inverseOverlay70: Color,
  val inverseOverlay80: Color
)

// ============================================================================
// Conversation Theme Colors (for wallpaer-aware bubbles)
// ============================================================================

@Immutable
data class ConversationThemeColors(
  val outgoingBodyColor: Color,
  val outgoingFooterColor: Color,
  val sendButtonTint: Color,
  val scrollForegoundColor: Color,
  val typingIndicatorForeground: Color,
  val typingIndicatorForegroundWallpaper: Color
)

// ============================================================================
// Full Color Scheme (Light/Dark)
// ============================================================================

/**
 * Complete color scheme for one mode (light or dark).
 * Contains all semantic color tokens organized by category.
 */
@Immutable
data class SignalColorScheme(
  val brand: BrandColors,
  val surface: SurfaceColors,
  val text: TextColors,
  val icon: IconColors,
  val interactive: InteractiveStateColors,
  val message: MessageColors,
  val status: StatusColors,
  val border: BorderColors,
  val component: ComponentColors,
  val transparency: TransparencyColors,
  val conversation: ConversationThemeColors,
  val isDarkMode: Boolean
)

// ============================================================================
// CompositionLocals for all color tokens
// ============================================================================

val LocalSignalColorScheme = staticCompositionLocalOf {
  // Will be overridden by SignalTheme
  SignalColorScheme(
    brand = BrandColors(
      primary = Color.Unspecified,
      primaryLight = Color.Unspecified,
      primaryDark = Color.Unspecified,
      primaryContainer = Color.Unspecified,
      onPrimary = Color.Unspecified,
      onPrimaryContainer = Color.Unspecified,
      secondary = Color.Unspecified,
      secondaryContainer = Color.Unspecified,
      onSecondary = Color.Unspecified,
      onSecondaryContainer = Color.Unspecified,
      accent = Color.Unspecified,
      accentLight = Color.Unspecified,
      accentDark = Color.Unspecified
    ),
    surface = SurfaceColors(
      background = Color.Unspecified,
      surface = Color.Unspecified,
      surface1 = Color.Unspecified,
      surface2 = Color.Unspecified,
      surface3 = Color.Unspecified,
      surface4 = Color.Unspecified,
      surface5 = Color.Unspecified,
      surfaceVariant = Color.Unspecified,
      surfaceHighest = Color.Unspecified,
      surfaceLowest = Color.Unspecified,
      dialog = Color.Unspecified,
      dialogSecondary = Color.Unspecified,
      card = Color.Unspecified,
      cardElevated = Color.Unspecified,
      sheet = Color.Unspecified,
      sheetScrim = Color.Unspecified,
      neutralSurface = Color.Unspecified
    ),
    text = TextColors(
      primary = Color.Unspecified,
      primaryDisabled = Color.Unspecified,
      primaryDialog = Color.Unspecified,
      secondary = Color.Unspecified,
      secondaryDisabled = Color.Unspecified,
      tertiary = Color.Unspecified,
      hint = Color.Unspecified,
      inverse = Color.Unspecified,
      onPrimary = Color.Unspecified,
      onSecondary = Color.Unspecified,
      onSurface = Color.Unspecified,
      onSurfaceVariant = Color.Unspecified,
      onBackground = Color.Unspecified,
      link = Color.Unspecified,
      destructive = Color.Unspecified
    ),
    icon = IconColors(
      primary = Color.Unspecified,
      secondary = Color.Unspecified,
      action = Color.Unspecified,
      onPrimary = Color.Unspecified,
      onSecondary = Color.Unspecified,
      tabSelected = Color.Unspecified,
      tabUnselected = Color.Unspecified,
      destructive = Color.Unspecified
    ),
    interactive = InteractiveStateColors(
      buttonPrimary = Color.Unspecified,
      buttonPrimaryPressed = Color.Unspecified,
      buttonPrimaryDisabled = Color.Unspecified,
      buttonPrimaryText = Color.Unspecified,
      buttonPrimaryTextDisabled = Color.Unspecified,
      buttonPrimaryRipple = Color.Unspecified,
      buttonSecondary = Color.Unspecified,
      buttonSecondaryPressed = Color.Unspecified,
      buttonSecondaryDisabled = Color.Unspecified,
      buttonSecondaryText = Color.Unspecified,
      buttonSecondaryTextDisabled = Color.Unspecified,
      buttonSecondaryStroke = Color.Unspecified,
      buttonSecondaryRipple = Color.Unspecified,
      buttonTonal = Color.Unspecified,
      buttonDanger = Color.Unspecified,
      buttonDangerText = Color.Unspecified,
      selection = Color.Unspecified,
      selectionDisabled = Color.Unspecified,
      focusRing = Color.Unspecified,
      hover = Color.Unspecified,
      hoverOverlay = Color.Unspecified,
      pressed = Color.Unspecified,
      pressedOverlay = Color.Unspecified,
      disabled = Color.Unspecified,
      disabledOverlay = Color.Unspecified,
      selected = Color.Unspecified,
      selectedOverlay = Color.Unspecified,
      touchHighlight = Color.Unspecified,
      ripple = Color.Unspecified,
      rippleInverse = Color.Unspecified,
      checkbox = Color.Unspecified,
      checkboxDisabled = Color.Unspecified,
      switch = Color.Unspecified,
      switchDisabled = Color.Unspecified,
      radio = Color.Unspecified,
      radioDisabled = Color.Unspecified,
      textCursor = Color.Unspecified,
      textHighlight = Color.Unspecified
    ),
    message = MessageColors(
      outgoingBubble = Color.Unspecified,
      incomingBubble = Color.Unspecified,
      outgoingBubbleWallpaper = Color.Unspecified,
      incomingBubbleWallpaper = Color.Unspecified,
      noBubble = Color.Unspecified,
      outgoingText = Color.Unspecified,
      outgoingTextSecondary = Color.Unspecified,
      incomingText = Color.Unspecified,
      incomingTextSecondary = Color.Unspecified,
      outgoingFooter = Color.Unspecified,
      incomingFooter = Color.Unspecified,
      updateText = Color.Unspecified,
      linkText = Color.Unspecified,
      sentIcon = Color.Unspecified,
      deliveredIcon = Color.Unspecified,
      readIcon = Color.Unspecified,
      errorIcon = Color.Unspecified,
      messagePending = Color.Unspecified,
      messageSending = Color.Unspecified,
      messageSent = Color.Unspecified,
      messageDelivered = Color.Unspecified,
      messageRead = Color.Unspecified,
      messageFailed = Color.Unspecified,
      messageExpiring = Color.Unspecified,
      unreadIndicator = Color.Unspecified,
      readIndicator = Color.Unspecified,
      unreadDot = Color.Unspecified,
      typingForeground = Color.Unspecified,
      typingForegroundWallpaper = Color.Unspecified,
      scrollToBottom = Color.Unspecified,
      selectedMessage = Color.Unspecified,
      selectedMessageOverlay = Color.Unspecified,
      quoteBackgroundIncoming = Color.Unspecified,
      quoteBackgroundOutgoing = Color.Unspecified,
      quoteBarIncoming = Color.Unspecified,
      quoteBarOutgoing = Color.Unspecified,
      quoteTextIncoming = Color.Unspecified,
      quoteTextOutgoing = Color.Unspecified,
      quoteLabelBackgroundIncoming = Color.Unspecified,
      quoteLabelBackgroundOutgoing = Color.Unspecified,
      quoteMissingIcon = Color.Unspecified,
      reactionPill = Color.Unspecified,
      reactionPillSelected = Color.Unspecified,
      reactionPillText = Color.Unspecified,
      reactionPillSelectedText = Color.Unspecified,
      reactionOverlay = Color.Unspecified,
      mediaThumbnailOverlay = Color.Unspecified,
      mediaThumbCircle = Color.Unspecified,
      audioForegroundOutgoing = Color.Unspecified,
      audioForegroundIncoming = Color.Unspecified,
      audioForegroundIncomingWallpaper = Color.Unspecified,
      audioPlayPauseBackgroundOutgoing = Color.Unspecified,
      audioPlayPauseBackgroundIncoming = Color.Unspecified,
      audioPlayPauseBackgroundIncomingWallpaper = Color.Unspecified,
      audioSeekBarPlayed = Color.Unspecified,
      audioSeekBarUnplayed = Color.Unspecified,
      viewOnceCircleIncoming = Color.Unspecified,
      viewOnceCircleIncomingWallpaper = Color.Unspecified,
      viewOnceUnopenedIncoming = Color.Unspecified,
      viewOnceOpenedIncoming = Color.Unspecified,
      viewOnceCircleOutgoing = Color.Unspecified,
      viewOnceCircleOutgoingWallpaper = Color.Unspecified,
      viewOnceUnopenedOutgoing = Color.Unspecified,
      viewOnceOpenedOutgoing = Color.Unspecified,
      mentionBackground = Color.Unspecified,
      pulseIncoming = Color.Unspecified,
      pulseOutgoing = Color.Unspecified,
      messageRequestContainer = Color.Unspecified,
      messageRequestBackground = Color.Unspecified,
      messageRequestDeny = Color.Unspecified,
      messageRequestAccept = Color.Unspecified,
      messageRequestContainerWallpaper = Color.Unspecified,
      messageRequestBackgroundWallpaper = Color.Unspecified,
      messageRequestDenyWallpaper = Color.Unspecified,
      messageRequestAcceptWallpaper = Color.Unspecified,
      safetyTipBackground = Color.Unspecified,
      safetyTipImageBackground = Color.Unspecified,
      sentGiftTint = Color.Unspecified,
      recvGiftTint = Color.Unspecified,
      stickerManagementAction = Color.Unspecified
    ),
    status = StatusColors(
      online = Color.Unspecified,
      offline = Color.Unspecified,
      away = Color.Unspecified,
      busy = Color.Unspecified,
      sent = Color.Unspecified,
      delivered = Color.Unspecified,
      read = Color.Unspecified,
      failed = Color.Unspecified,
      pending = Color.Unspecified,
      verified = Color.Unspecified,
      unverified = Color.Unspecified,
      safetyNumber = Color.Unspecified,
      error = Color.Unspecified,
      errorContainer = Color.Unspecified,
      warning = Color.Unspecified,
      warningOn = Color.Unspecified,
      warningContainer = Color.Unspecified,
      success = Color.Unspecified,
      successContainer = Color.Unspecified,
      info = Color.Unspecified,
      infoContainer = Color.Unspecified,
      unreadBadge = Color.Unspecified,
      unreadBadgeText = Color.Unspecified
    ),
    border = BorderColors(
      dividerMinor = Color.Unspecified,
      dividerMajor = Color.Unspecified,
      dividerInset = Color.Unspecified,
      border = Color.Unspecified,
      borderFocus = Color.Unspecified,
      borderError = Color.Unspecified,
      borderWarning = Color.Unspecified,
      outline = Color.Unspecified,
      outlineVariant = Color.Unspecified
    ),
    component = ComponentColors(
      toolbarBackground = Color.Unspecified,
      toolbarBackgroundScrolled = Color.Unspecified,
      toolbarBackgroundWallpaper = Color.Unspecified,
      toolbarBackgroundWallpaperScrolled = Color.Unspecified,
      toolbarBackgroundIncognito = Color.Unspecified,
      toolbarForeground = Color.Unspecified,
      toolbarSubtitle = Color.Unspecified,
      fabBackground = Color.Unspecified,
      fabForeground = Color.Unspecified,
      fabSecondaryBackground = Color.Unspecified,
      fabSecondaryForeground = Color.Unspecified,
      searchBackground = Color.Unspecified,
      searchHint = Color.Unspecified,
      searchIcon = Color.Unspecified,
      conversationListSelected = Color.Unspecified,
      conversationListArchiveStart = Color.Unspecified,
      conversationListArchiveEnd = Color.Unspecified,
      conversationListUnread = Color.Unspecified,
      navigationBar = Color.Unspecified,
      navigationBarScrim = Color.Unspecified,
      navigationItemActive = Color.Unspecified,
      navigationItemInactive = Color.Unspecified,
      bottomSheetBackground = Color.Unspecified,
      bottomSheetScrim = Color.Unspecified,
      bottomSheetNavigationBar = Color.Unspecified,
      snackbarBackground = Color.Unspecified,
      snackbarForeground = Color.Unspecified,
      snackbarAction = Color.Unspecified,
      toastBackground = Color.Unspecified,
      toastForeground = Color.Unspecified,
      tooltipBackground = Color.Unspecified,
      tooltipForeground = Color.Unspecified,
      composerBackground = Color.Unspecified,
      composerDivider = Color.Unspecified,
      composerText = Color.Unspecified,
      composerHint = Color.Unspecified,
      composerAttachButton = Color.Unspecified,
      composerSendButton = Color.Unspecified,
      composerMediaKeyboardBg = Color.Unspecified,
      composerMediaKeyboardBarBg = Color.Unspecified,
      mediaPreviewBarBackground = Color.Unspecified,
      mediaOverviewToolbarBackground = Color.Unspecified,
      mediaOverviewToolbarSecondary = Color.Unspecified,
      mediaOverviewToolbarForeground = Color.Unspecified,
      mediaOverviewHeaderForeground = Color.Unspecified,
      cameraIconBackground = Color.Unspecified,
      contactCheckboxBackground = Color.Unspecified,
      contactFilterToolbarIcon = Color.Unspecified,
      wallpaperPreviewBackground = Color.Unspecified,
      wallpaperComposeBackground = Color.Unspecified,
      wallpaperBubble = Color.Unspecified,
      wallpaperNavigation = Color.Unspecified,
      storyBullet = Color.Unspecified,
      storyGradientStart = Color.Unspecified,
      storyCaptionGradientStart = Color.Unspecified,
      paymentCardOverflow = Color.Unspecified,
      paymentCurrencyCode = Color.Unspecified,
      insightTitle = Color.Unspecified,
      insightBody = Color.Unspecified,
      insightProgressBackground = Color.Unspecified,
      qrCardColor = Color.Unspecified,
      qrCardTextColor = Color.Unspecified,
      releaseNotesBubble = Color.Unspecified,
      releaseNotesBubbleText = Color.Unspecified,
      releaseNotesCtaBackground = Color.Unspecified,
      releaseNotesBackgroundPattern = Color.Unspecified,
      releaseNotesHeaderBorder = Color.Unspecified,
      releaseNotesBackground = Color.Unspecified,
      releaseNotesHeaderBackground = Color.Unspecified,
      releaseNotesToolbarScrolled = Color.Unspecified,
      releaseNotesToolbarTransparent = Color.Unspecified,
      megaphoneBackground = Color.Unspecified,
      megaphoneBodyText = Color.Unspecified,
      voiceNotePlayerBackground = Color.Unspecified,
      numPadKeyBackground = Color.Unspecified,
      attachmentKeyboardForeground = Color.Unspecified,
      loginTopBackground = Color.Unspecified,
      groupStoryIndicator = Color.Unspecified,
      reactWithAnyBackground = Color.Unspecified,
      reactWithAnySearchBackground = Color.Unspecified,
      reactWithAnySearchHint = Color.Unspecified,
      reactWithAnyCustomizeBackground = Color.Unspecified,
      settingsRipple = Color.Unspecified,
      lowPriorityButtonText = Color.Unspecified
    ),
    transparency = TransparencyColors(
      transparent = Color.Unspecified,
      overlay5 = Color.Unspecified,
      overlay10 = Color.Unspecified,
      overlay15 = Color.Unspecified,
      overlay20 = Color.Unspecified,
      overlay25 = Color.Unspecified,
      overlay30 = Color.Unspecified,
      overlay40 = Color.Unspecified,
      overlay50 = Color.Unspecified,
      overlay60 = Color.Unspecified,
      overlay70 = Color.Unspecified,
      overlay80 = Color.Unspecified,
      inverseTransparent = Color.Unspecified,
      inverseOverlay5 = Color.Unspecified,
      inverseOverlay10 = Color.Unspecified,
      inverseOverlay15 = Color.Unspecified,
      inverseOverlay20 = Color.Unspecified,
      inverseOverlay25 = Color.Unspecified,
      inverseOverlay30 = Color.Unspecified,
      inverseOverlay40 = Color.Unspecified,
      inverseOverlay50 = Color.Unspecified,
      inverseOverlay60 = Color.Unspecified,
      inverseOverlay70 = Color.Unspecified,
      inverseOverlay80 = Color.Unspecified
    ),
    conversation = ConversationThemeColors(
      outgoingBodyColor = Color.Unspecified,
      outgoingFooterColor = Color.Unspecified,
      sendButtonTint = Color.Unspecified,
      scrollForegoundColor = Color.Unspecified,
      typingIndicatorForeground = Color.Unspecified,
      typingIndicatorForegroundWallpaper = Color.Unspecified
    ),
    isDarkMode = false
  )
}

// ============================================================================
// Light & Dark Color Scheme Factories
// ============================================================================

/** Create the light color scheme with all semantic tokens. */
internal fun createLightColorScheme(): SignalColorScheme = SignalColorScheme(
  brand = BrandColors(
    primary = Color(0xFF2C58C3),
    primaryLight = Color(0xFF6191F3),
    primaryDark = Color(0xFF1851B4),
    primaryContainer = Color(0xFFD2DFFB),
    onPrimary = Color(0xFFFFFFFF),
    onPrimaryContainer = Color(0xFF051845),
    secondary = Color(0xFF586071),
    secondaryContainer = Color(0xFFDCE5F9),
    onSecondary = Color(0xFFFFFFFF),
    onSecondaryContainer = Color(0xFF151D2C),
    accent = Color(0xFF2C6BED),
    accentLight = Color(0xFF6191F3),
    accentDark = Color(0xFF1851B4)
  ),
  surface = SurfaceColors(
    background = Color(0xFFFBFCFF),
    surface = Color(0xFFFBFCFF),
    surface1 = Color(0xFFF2F5F9),
    surface2 = Color(0xFFEDF0F6),
    surface3 = Color(0xFFE8ECF4),
    surface4 = Color(0xFFE6EAF3),
    surface5 = Color(0xFFE3E7F1),
    surfaceVariant = Color(0xFFE7EBF3),
    surfaceHighest = Color(0xFFE7EBF3),
    surfaceLowest = Color(0xFFFFFFFF),
    dialog = Color(0xFFEDF0F6),
    dialogSecondary = Color(0xFFF2F5F9),
    card = Color(0xFFFFFFFF),
    cardElevated = Color(0xFFF2F5F9),
    sheet = Color(0xFFF2F5F9),
    sheetScrim = Color(0x99000000),
    neutralSurface = Color(0x99FFFFFF)
  ),
  text = TextColors(
    primary = Color(0xFF1B1B1D),
    primaryDisabled = Color(0xFFB9B9B9),
    primaryDialog = Color(0xFF525252),
    secondary = Color(0xFF545863),
    secondaryDisabled = Color(0xFFD4D4D4),
    tertiary = Color(0xFF848484),
    hint = Color(0xFF848484),
    inverse = Color(0xFFFFFFFF),
    onPrimary = Color(0xFFFFFFFF),
    onSecondary = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1B1B1D),
    onSurfaceVariant = Color(0xFF545863),
    onBackground = Color(0xFF1B1D1D),
    link = Color(0xFF2C6BED),
    destructive = Color(0xFFBA1B1B)
  ),
  icon = IconColors(
    primary = Color(0xFF1B1B1D),
    secondary = Color(0xFF545863),
    action = Color(0xFF000000),
    onPrimary = Color(0xFFD2DFFB),
    onSecondary = Color(0xFFDCE5F9),
    tabSelected = Color(0xFF1B1B1D),
    tabUnselected = Color(0xFF545863),
    destructive = Color(0xFFBA1B1B)
  ),
  interactive = InteractiveStateColors(
    buttonPrimary = Color(0xFF2C58C3),
    buttonPrimaryPressed = Color(0xFF1851B4),
    buttonPrimaryDisabled = Color(0x332C58C3),
    buttonPrimaryText = Color(0xFFFFFFFF),
    buttonPrimaryTextDisabled = Color(0x33FFFFFF),
    buttonPrimaryRipple = Color(0x26000000),
    buttonSecondary = Color(0xFFE9E9E9),
    buttonSecondaryPressed = Color(0xFFD4D4D4),
    buttonSecondaryDisabled = Color(0x33E9E9E9),
    buttonSecondaryText = Color(0xFF2C58C3),
    buttonSecondaryTextDisabled = Color(0xFFB9B9B9),
    buttonSecondaryStroke = Color(0xFFD4D4D4),
    buttonSecondaryRipple = Color(0x0DE9E9E9),
    buttonTonal = Color(0xFFD2DFFB),
    buttonDanger = Color(0xFFEF5350),
    buttonDangerText = Color(0xFFFFFFFF),
    selection = Color(0xFFD2DFFB),
    selectionDisabled = Color(0x1AB9B9B9),
    focusRing = Color(0xFF2C58C3),
    hover = Color(0x08000000),
    hoverOverlay = Color(0x08000000),
    pressed = Color(0x1A000000),
    pressedOverlay = Color(0x1A000000),
    disabled = Color(0x61000000),
    disabledOverlay = Color(0x08000000),
    selected = Color(0xFFD2DFFB),
    selectedOverlay = Color(0x1A2C58C3),
    touchHighlight = Color(0x400099CC),
    ripple = Color(0x40000000),
    rippleInverse = Color(0x40FFFFFF),
    checkbox = Color(0xFF2C58C3),
    checkboxDisabled = Color(0xFFB9B9B9),
    switch = Color(0xFF2C58C3),
    switchDisabled = Color(0xFFB9B9B9),
    radio = Color(0xFF2C58C3),
    radioDisabled = Color(0xFFB9B9B9),
    textCursor = Color(0xFF2C58C3),
    textHighlight = Color(0xFFD2DFFB)
  ),
  message = MessageColors(
    outgoingBubble = Color(0x00000000),
    incomingBubble = Color(0xFFE7EBF3),
    outgoingBubbleWallpaper = Color(0x00000000),
    incomingBubbleWallpaper = Color(0x0A000000),
    noBubble = Color(0x00000000),
    outgoingText = Color(0xFFFFFFFF),
    outgoingTextSecondary = Color(0xCCFFFFFF),
    incomingText = Color(0xFF1B1B1D),
    incomingTextSecondary = Color(0xFF545863),
    outgoingFooter = Color(0x80FFFFFF),
    incomingFooter = Color(0xFF545863),
    updateText = Color(0xFF545863),
    linkText = Color(0xFF2C6BED),
    sentIcon = Color(0xFF545863),
    deliveredIcon = Color(0xFF545863),
    readIcon = Color(0xFF545863),
    errorIcon = Color(0xFFBA1B1B),
    messagePending = Color(0xFF545863),
    messageSending = Color(0xFF545863),
    messageSent = Color(0xFF545863),
    messageDelivered = Color(0xFF545863),
    messageRead = Color(0xFF545863),
    messageFailed = Color(0xFFBA1B1B),
    messageExpiring = Color(0xFFBA1B1B),
    unreadIndicator = Color(0xFFCE4A40),
    readIndicator = Color(0xFF545863),
    unreadDot = Color(0xFFCE4A40),
    typingForeground = Color(0xFF545863),
    typingForegroundWallpaper = Color(0xFF5C5C5C),
    scrollToBottom = Color(0xFF545863),
    selectedMessage = Color(0xFFD2DFFB),
    selectedMessageOverlay = Color(0x1A2C58C3),
    quoteBackgroundIncoming = Color(0x14FFFFFF),
    quoteBackgroundOutgoing = Color(0x14FFFFFF),
    quoteBarIncoming = Color(0xFF2C6BED),
    quoteBarOutgoing = Color(0xFF2C6BED),
    quoteTextIncoming = Color(0xFF1B1B1D),
    quoteTextOutgoing = Color(0xFF1B1B1D),
    quoteLabelBackgroundIncoming = Color(0x0D000000),
    quoteLabelBackgroundOutgoing = Color(0x0D000000),
    quoteMissingIcon = Color(0xFF5E5E5E),
    reactionPill = Color(0xFFE7EBF3),
    reactionPillSelected = Color(0xFFD2DFFB),
    reactionPillText = Color(0xFF545863),
    reactionPillSelectedText = Color(0xFF2C58C3),
    reactionOverlay = Color(0x3370747D),
    mediaThumbnailOverlay = Color(0x14FFFFFF),
    mediaThumbCircle = Color(0x00FFFFFF),
    audioForegroundOutgoing = Color(0xFFFFFFFF),
    audioForegroundIncoming = Color(0xFF1B1B1D),
    audioForegroundIncomingWallpaper = Color(0xFFFFFFFF),
    audioPlayPauseBackgroundOutgoing = Color(0x08000000),
    audioPlayPauseBackgroundIncoming = Color(0x80FFFFFF),
    audioPlayPauseBackgroundIncomingWallpaper = Color(0x29000000),
    audioSeekBarPlayed = Color(0xFF5E5E5E),
    audioSeekBarUnplayed = Color(0xFFB9B9B9),
    viewOnceCircleIncoming = Color(0xCCFFFFFF),
    viewOnceCircleIncomingWallpaper = Color(0xFFE9E9E9),
    viewOnceUnopenedIncoming = Color(0xFF1B1B1D),
    viewOnceOpenedIncoming = Color(0xFF777777),
    viewOnceCircleOutgoing = Color(0x33FFFFFF),
    viewOnceCircleOutgoingWallpaper = Color(0x33FFFFFF),
    viewOnceUnopenedOutgoing = Color(0xFFFFFFFF),
    viewOnceOpenedOutgoing = Color(0xE6FFFFFF),
    mentionBackground = Color(0xFFC6C6C6),
    pulseIncoming = Color(0x18000000),
    pulseOutgoing = Color(0x40000000),
    messageRequestContainer = Color(0xFFFBFCFF),
    messageRequestBackground = Color(0xFFEDF0F6),
    messageRequestDeny = Color(0xFFBA1B1B),
    messageRequestAccept = Color(0xFFDCE5F9),
    messageRequestContainerWallpaper = Color(0x14FFFFFF),
    messageRequestBackgroundWallpaper = Color(0x80FFFFFF),
    messageRequestDenyWallpaper = Color(0xFFBA1B1B),
    messageRequestAcceptWallpaper = Color(0xFFE2E1E5),
    safetyTipBackground = Color(0xFFFFFFFF),
    safetyTipImageBackground = Color(0x99F2F5F9),
    sentGiftTint = Color(0xB2FFFFFF),
    recvGiftTint = Color(0xCCFFFFFF),
    stickerManagementAction = Color(0xFF1B1B1D)
  ),
  status = StatusColors(
    online = Color(0xFF4CAF50),
    offline = Color(0xFF777777),
    away = Color(0xFFFFC107),
    busy = Color(0xFFEF5350),
    sent = Color(0xFF545863),
    delivered = Color(0xFF545863),
    read = Color(0xFF545863),
    failed = Color(0xFFBA1B1B),
    pending = Color(0xFF545863),
    verified = Color(0xFF4CAF50),
    unverified = Color(0xFF777777),
    safetyNumber = Color(0xFF506ECD),
    error = Color(0xFFBA1B1B),
    errorContainer = Color(0xFFFFDAD4),
    warning = Color(0xFFB44828),
    warningOn = Color(0xFFB44828),
    warningContainer = Color(0x1FB44828),
    success = Color(0xFF4CAF50),
    successContainer = Color(0xFFE8F5E9),
    info = Color(0xFF2C6BED),
    infoContainer = Color(0xFFD2DFFB),
    unreadBadge = Color(0xFFCE4A40),
    unreadBadgeText = Color(0xFFFFFFFF)
  ),
  border = BorderColors(
    dividerMinor = Color(0xFFD4D4D4),
    dividerMajor = Color(0xFFB9B9B9),
    dividerInset = Color(0xFFE7EBF3),
    border = Color(0xFFD4D4D4),
    borderFocus = Color(0xFF2C58C3),
    borderError = Color(0xFFBA1B1B),
    borderWarning = Color(0xFFB44828),
    outline = Color(0xFF808389),
    outlineVariant = Color(0xFFE7EBF3)
  ),
  component = ComponentColors(
    toolbarBackground = Color(0xFFFBFCFF),
    toolbarBackgroundScrolled = Color(0xFFF2F5F9),
    toolbarBackgroundWallpaper = Color(0x99FFFFFF),
    toolbarBackgroundWallpaperScrolled = Color(0x99FFFFFF),
    toolbarBackgroundIncognito = Color(0xFFC4A8E0),
    toolbarForeground = Color(0xFFFFFFFF),
    toolbarSubtitle = Color(0xE6FFFFFF),
    fabBackground = Color(0xFF2C58C3),
    fabForeground = Color(0xFFFFFFFF),
    fabSecondaryBackground = Color(0xFFD2DFFB),
    fabSecondaryForeground = Color(0xFF2C58C3),
    searchBackground = Color(0xFFEDF0F6),
    searchHint = Color(0xFF848484),
    searchIcon = Color(0xFF1B1B1D),
    conversationListSelected = Color(0xFFE8ECF4),
    conversationListArchiveStart = Color(0xFF2C58C3),
    conversationListArchiveEnd = Color(0xFF2C58C3),
    conversationListUnread = Color(0xFFCE4A40),
    navigationBar = Color(0xFFFBFCFF),
    navigationBarScrim = Color(0x33000000),
    navigationItemActive = Color(0xFF2C58C3),
    navigationItemInactive = Color(0xFF848484),
    bottomSheetBackground = Color(0xFFF2F5F9),
    bottomSheetScrim = Color(0x99000000),
    bottomSheetNavigationBar = Color(0xFFFBFCFF),
    snackbarBackground = Color(0xFF1B1B1D),
    snackbarForeground = Color(0xFFE2E1E5),
    snackbarAction = Color(0xFFB6C5FA),
    toastBackground = Color(0xFFFFFFFF),
    toastForeground = Color(0xFF1B1B1D),
    tooltipBackground = Color(0xFFDCE5F9),
    tooltipForeground = Color(0xFF1B1B1D),
    composerBackground = Color(0xFFFBFCFF),
    composerDivider = Color(0x32000000),
    composerText = Color(0xFF1B1B1D),
    composerHint = Color(0xFF848484),
    composerAttachButton = Color(0xFF545863),
    composerSendButton = Color(0xFF2C58C3),
    composerMediaKeyboardBg = Color(0xFFEDF0F6),
    composerMediaKeyboardBarBg = Color(0xFFE3E7F1),
    mediaPreviewBarBackground = Color(0x90010101),
    mediaOverviewToolbarBackground = Color(0xFFFFFFFF),
    mediaOverviewToolbarSecondary = Color(0xFFF6F6F6),
    mediaOverviewToolbarForeground = Color(0xFF474747),
    mediaOverviewHeaderForeground = Color(0xFF777777),
    cameraIconBackground = Color(0xFFF6F6F6),
    contactCheckboxBackground = Color(0xFFFFFFFF),
    contactFilterToolbarIcon = Color(0xFF545863),
    wallpaperPreviewBackground = Color(0x4DFFFFFF),
    wallpaperComposeBackground = Color(0xB8FFFFFF),
    wallpaperBubble = Color(0x80FFFFFF),
    wallpaperNavigation = Color(0x80FFFFFF),
    storyBullet = Color(0xFFC6C6C6),
    storyGradientStart = Color(0x99000000),
    storyCaptionGradientStart = Color(0xCC000000),
    paymentCardOverflow = Color(0xFF848484),
    paymentCurrencyCode = Color(0xFF848484),
    insightTitle = Color(0xFF1B1B1D),
    insightBody = Color(0xFF5E5E5E),
    insightProgressBackground = Color(0xFFD4D4D4),
    qrCardColor = Color(0xFFF2F5F9),
    qrCardTextColor = Color(0xFF545863),
    releaseNotesBubble = Color(0xFF9294BC),
    releaseNotesBubbleText = Color(0xFFFFFFFF),
    releaseNotesCtaBackground = Color(0xFFD0D1E9),
    releaseNotesBackgroundPattern = Color(0xFFE3E4E9),
    releaseNotesHeaderBorder = Color(0xFFE3E4E9),
    releaseNotesBackground = Color(0xFFF3F3F7),
    releaseNotesHeaderBackground = Color(0xFFF6F7FF),
    releaseNotesToolbarScrolled = Color(0xFFEAEDF8),
    releaseNotesToolbarTransparent = Color(0x00EAEDF8),
    megaphoneBackground = Color(0xFFFFFFFF),
    megaphoneBodyText = Color(0xFF545863),
    voiceNotePlayerBackground = Color(0xFFF2F5F9),
    numPadKeyBackground = Color(0xFFDCE5F9),
    attachmentKeyboardForeground = Color(0xFF525252),
    loginTopBackground = Color(0xFF2C6BED),
    groupStoryIndicator = Color(0xFF2C6BED),
    reactWithAnyBackground = Color(0xFFF2F5F9),
    reactWithAnySearchBackground = Color(0xFFE7EBF3),
    reactWithAnySearchHint = Color(0xFF545863),
    reactWithAnyCustomizeBackground = Color(0xFFE7EBF3),
    settingsRipple = Color(0x18000000),
    lowPriorityButtonText = Color(0xFF474747)
  ),
  transparency = TransparencyColors(
    transparent = Color(0x00FFFFFF),
    overlay5 = Color(0x0D000000),
    overlay10 = Color(0x18000000),
    overlay15 = Color(0x26000000),
    overlay20 = Color(0x33000000),
    overlay25 = Color(0x40000000),
    overlay30 = Color(0x4D000000),
    overlay40 = Color(0x66000000),
    overlay50 = Color(0x80000000),
    overlay60 = Color(0x99000000),
    overlay70 = Color(0xB3000000),
    overlay80 = Color(0xCC000000),
    inverseTransparent = Color(0x00FFFFFF),
    inverseOverlay5 = Color(0x0DFFFFFF),
    inverseOverlay10 = Color(0x18FFFFFF),
    inverseOverlay15 = Color(0x26FFFFFF),
    inverseOverlay20 = Color(0x33FFFFFF),
    inverseOverlay25 = Color(0x40FFFFFF),
    inverseOverlay30 = Color(0x4DFFFFFF),
    inverseOverlay40 = Color(0x66FFFFFF),
    inverseOverlay50 = Color(0x80FFFFFF),
    inverseOverlay60 = Color(0x99FFFFFF),
    inverseOverlay70 = Color(0xB2FFFFFF),
    inverseOverlay80 = Color(0xCCFFFFFF)
  ),
  conversation = ConversationThemeColors(
    outgoingBodyColor = Color(0xFFFFFFFF),
    outgoingFooterColor = Color(0x80FFFFFF),
    sendButtonTint = Color(0xFFFFFFFF),
    scrollForegoundColor = Color(0xFF545863),
    typingIndicatorForeground = Color(0xFF545863),
    typingIndicatorForegroundWallpaper = Color(0xFF5C5C5C)
  ),
  isDarkMode = false
)

/** Create the dark color scheme with all semantic tokens. */
internal fun createDarkColorScheme(): SignalColorScheme = SignalColorScheme(
  brand = BrandColors(
    primary = Color(0xFFB6C5FA),
    primaryLight = Color(0xFFDBE1FC),
    primaryDark = Color(0xFF464B5C),
    primaryContainer = Color(0xFF464B5C),
    onPrimary = Color(0xFF1E2438),
    onPrimaryContainer = Color(0xFFDBE1FC),
    secondary = Color(0xFFC1C6DD),
    secondaryContainer = Color(0xFF414659),
    onSecondary = Color(0xFF2A3042),
    onSecondaryContainer = Color(0xFFDCE1F9),
    accent = Color(0xFF6191F3),
    accentLight = Color(0xFFDBE1FC),
    accentDark = Color(0xFF464B5C)
  ),
  surface = SurfaceColors(
    background = Color(0xFF1B1C1F),
    surface = Color(0xFF1B1C1F),
    surface1 = Color(0xFF23242A),
    surface2 = Color(0xFF272A31),
    surface3 = Color(0xFF2C2F37),
    surface4 = Color(0xFF2E3039),
    surface5 = Color(0xFF31343E),
    surfaceVariant = Color(0xFF303133),
    surfaceHighest = Color(0xFF303133),
    surfaceLowest = Color(0xFF121212),
    dialog = Color(0xFF272A31),
    dialogSecondary = Color(0xFF23242A),
    card = Color(0xFF23242A),
    cardElevated = Color(0xFF272A31),
    sheet = Color(0xFF23242A),
    sheetScrim = Color(0x14FFFFFF),
    neutralSurface = Color(0x14FFFFFF)
  ),
  text = TextColors(
    primary = Color(0xFFE2E1E5),
    primaryDisabled = Color(0xFF5E5E5E),
    primaryDialog = Color(0xFFB9B9B9),
    secondary = Color(0xFFBEBFC5),
    secondaryDisabled = Color(0xFF5E5E5E),
    tertiary = Color(0xFF848484),
    hint = Color(0xFFB9B9B9),
    inverse = Color(0xFF121212),
    onPrimary = Color(0xFF1E2438),
    onSecondary = Color(0xFF2A3042),
    onSurface = Color(0xFFE2E1E5),
    onSurfaceVariant = Color(0xFFBEBFC5),
    onBackground = Color(0xFFE2E1E5),
    link = Color(0xFF6191F3),
    destructive = Color(0xFFFFB4A9)
  ),
  icon = IconColors(
    primary = Color(0xFFB9B9B9),
    secondary = Color(0xFF5E5E5E),
    action = Color(0xFFB9B9B9),
    onPrimary = Color(0xFF464B5C),
    onSecondary = Color(0xFF414659),
    tabSelected = Color(0xFFE2E1E5),
    tabUnselected = Color(0xFFBEBFC5),
    destructive = Color(0xFFFFB4A9)
  ),
  interactive = InteractiveStateColors(
    buttonPrimary = Color(0xFFB6C5FA),
    buttonPrimaryPressed = Color(0xFFDBE1FC),
    buttonPrimaryDisabled = Color(0x33B6C5FA),
    buttonPrimaryText = Color(0xFF1E2438),
    buttonPrimaryTextDisabled = Color(0x331E2438),
    buttonPrimaryRipple = Color(0x26FFFFFF),
    buttonSecondary = Color(0xFF3B3B3B),
    buttonSecondaryPressed = Color(0xFF5E5E5E),
    buttonSecondaryDisabled = Color(0x333B3B3B),
    buttonSecondaryText = Color(0xFFB6C5FA),
    buttonSecondaryTextDisabled = Color(0xFF5E5E5E),
    buttonSecondaryStroke = Color(0xFF5E5E5E),
    buttonSecondaryRipple = Color(0x0DFFFFFF),
    buttonTonal = Color(0xFF464B5C),
    buttonDanger = Color(0xFFFFB4A9),
    buttonDangerText = Color(0xFF121212),
    selection = Color(0xFF464B5C),
    selectionDisabled = Color(0x1A5E5E5E),
    focusRing = Color(0xFFB6C5FA),
    hover = Color(0x08FFFFFF),
    hoverOverlay = Color(0x08FFFFFF),
    pressed = Color(0x1AFFFFFF),
    pressedOverlay = Color(0x1AFFFFFF),
    disabled = Color(0x61FFFFFF),
    disabledOverlay = Color(0x08FFFFFF),
    selected = Color(0xFF464B5C),
    selectedOverlay = Color(0x1AB6C5FA),
    touchHighlight = Color(0x40E2E1E5),
    ripple = Color(0x40FFFFFF),
    rippleInverse = Color(0x40000000),
    checkbox = Color(0xFFB6C5FA),
    checkboxDisabled = Color(0xFF5E5E5E),
    switch = Color(0xFFB6C5FA),
    switchDisabled = Color(0xFF5E5E5E),
    radio = Color(0xFFB6C5FA),
    radioDisabled = Color(0xFF5E5E5E),
    textCursor = Color(0xFFB6C5FA),
    textHighlight = Color(0xFF464B5C)
  ),
  message = MessageColors(
    outgoingBubble = Color(0x00000000),
    incomingBubble = Color(0xFF303133),
    outgoingBubbleWallpaper = Color(0x00000000),
    incomingBubbleWallpaper = Color(0xF51A1A1A),
    noBubble = Color(0x00000000),
    outgoingText = Color(0xE0FFFFFF),
    outgoingTextSecondary = Color(0x99FFFFFF),
    incomingText = Color(0xFFE2E1E5),
    incomingTextSecondary = Color(0xFFBEBFC5),
    outgoingFooter = Color(0xA3FFFFFF),
    incomingFooter = Color(0xFFBEBFC5),
    updateText = Color(0xFFBEBFC5),
    linkText = Color(0xFF6191F3),
    sentIcon = Color(0xFFBEBFC5),
    deliveredIcon = Color(0xFFBEBFC5),
    readIcon = Color(0xFFBEBFC5),
    errorIcon = Color(0xFFFFB4A9),
    messagePending = Color(0xFFBEBFC5),
    messageSending = Color(0xFFBEBFC5),
    messageSent = Color(0xFFBEBFC5),
    messageDelivered = Color(0xFFBEBFC5),
    messageRead = Color(0xFFBEBFC5),
    messageFailed = Color(0xFFFFB4A9),
    messageExpiring = Color(0xFFFFB4A9),
    unreadIndicator = Color(0xFFCE4A40),
    readIndicator = Color(0xFFBEBFC5),
    unreadDot = Color(0xFFCE4A40),
    typingForeground = Color(0xFFBEBFC5),
    typingForegroundWallpaper = Color(0xFFA3FFFFFF),
    scrollToBottom = Color(0xFFBEBFC5),
    selectedMessage = Color(0xFF464B5C),
    selectedMessageOverlay = Color(0x1AB6C5FA),
    quoteBackgroundIncoming = Color(0x1FFFFFFF),
    quoteBackgroundOutgoing = Color(0x1FFFFFFF),
    quoteBarIncoming = Color(0xFF6191F3),
    quoteBarOutgoing = Color(0xFF6191F3),
    quoteTextIncoming = Color(0xFFE2E1E5),
    quoteTextOutgoing = Color(0xFFE2E1E5),
    quoteLabelBackgroundIncoming = Color(0x1FFFFFFF),
    quoteLabelBackgroundOutgoing = Color(0x1FFFFFFF),
    quoteMissingIcon = Color(0xFFE9E9E9),
    reactionPill = Color(0xFF303133),
    reactionPillSelected = Color(0xFF464B5C),
    reactionPillText = Color(0xFFBEBFC5),
    reactionPillSelectedText = Color(0xFFB6C5FA),
    reactionOverlay = Color(0xD91B1C1F),
    mediaThumbnailOverlay = Color(0x0AFFFFFF),
    mediaThumbCircle = Color(0x00000000),
    audioForegroundOutgoing = Color(0xFFE2E1E5),
    audioForegroundIncoming = Color(0xFFE2E1E5),
    audioForegroundIncomingWallpaper = Color(0xFFE2E1E5),
    audioPlayPauseBackgroundOutgoing = Color(0x29FFFFFF),
    audioPlayPauseBackgroundIncoming = Color(0x29FFFFFF),
    audioPlayPauseBackgroundIncomingWallpaper = Color(0x29FFFFFF),
    audioSeekBarPlayed = Color(0xFFB9B9B9),
    audioSeekBarUnplayed = Color(0xFF5E5E5E),
    viewOnceCircleIncoming = Color(0xFF5E5E5E),
    viewOnceCircleIncomingWallpaper = Color(0xFF3B3B3B),
    viewOnceUnopenedIncoming = Color(0xFFE9E9E9),
    viewOnceOpenedIncoming = Color(0xCCFFFFFF),
    viewOnceCircleOutgoing = Color(0x33FFFFFF),
    viewOnceCircleOutgoingWallpaper = Color(0x33FFFFFF),
    viewOnceUnopenedOutgoing = Color(0xFFE9E9E9),
    viewOnceOpenedOutgoing = Color(0xCCFFFFFF),
    mentionBackground = Color(0xFF5E5E5E),
    pulseIncoming = Color(0x26FFFFFF),
    pulseOutgoing = Color(0x40FFFFFF),
    messageRequestContainer = Color(0xFF1B1C1F),
    messageRequestBackground = Color(0xFF272A31),
    messageRequestDeny = Color(0xFFFFB4A9),
    messageRequestAccept = Color(0xFF414659),
    messageRequestContainerWallpaper = Color(0xF51A1A1A),
    messageRequestBackgroundWallpaper = Color(0x18FFFFFF),
    messageRequestDenyWallpaper = Color(0xFFFFB4A9),
    messageRequestAcceptWallpaper = Color(0xFFE2E1E5),
    safetyTipBackground = Color(0xFF414659),
    safetyTipImageBackground = Color(0xFF2A3042),
    sentGiftTint = Color(0x33FFFFFF),
    recvGiftTint = Color(0x33FFFFFF),
    stickerManagementAction = Color(0xFFB9B9B9)
  ),
  status = StatusColors(
    online = Color(0xFF4CAF50),
    offline = Color(0xFF5E5E5E),
    away = Color(0xFFFFC107),
    busy = Color(0xFFFFB4A9),
    sent = Color(0xFFBEBFC5),
    delivered = Color(0xFFBEBFC5),
    read = Color(0xFFBEBFC5),
    failed = Color(0xFFFFB4A9),
    pending = Color(0xFFBEBFC5),
    verified = Color(0xFF4CAF50),
    unverified = Color(0xFF5E5E5E),
    safetyNumber = Color(0xFF506ECD),
    error = Color(0xFFFFB4A9),
    errorContainer = Color(0xFF930006),
    warning = Color(0xFFEB977D),
    warningOn = Color(0xFFEB977D),
    warningContainer = Color(0x1FEB977D),
    success = Color(0xFF4CAF50),
    successContainer = Color(0xFF1B321B),
    info = Color(0xFF6191F3),
    infoContainer = Color(0xFF464B5C),
    unreadBadge = Color(0xFFCE4A40),
    unreadBadgeText = Color(0xFFFFFFFF)
  ),
  border = BorderColors(
    dividerMinor = Color(0xFF3B3B3B),
    dividerMajor = Color(0xFF5E5E5E),
    dividerInset = Color(0xFF303133),
    border = Color(0xFF3B3B3B),
    borderFocus = Color(0xFFB6C5FA),
    borderError = Color(0xFFFFB4A9),
    borderWarning = Color(0xFFEB977D),
    outline = Color(0xFF5C5E65),
    outlineVariant = Color(0xFF303133)
  ),
  component = ComponentColors(
    toolbarBackground = Color(0xFF1B1C1F),
    toolbarBackgroundScrolled = Color(0xFF23242A),
    toolbarBackgroundWallpaper = Color(0xF5000000),
    toolbarBackgroundWallpaperScrolled = Color(0xF5000000),
    toolbarBackgroundIncognito = Color(0xFF4A2D73),
    toolbarForeground = Color(0xFFE2E1E5),
    toolbarSubtitle = Color(0xCCFFFFFF),
    fabBackground = Color(0xFFB6C5FA),
    fabForeground = Color(0xFF1E2438),
    fabSecondaryBackground = Color(0xFF464B5C),
    fabSecondaryForeground = Color(0xFFDBE1FC),
    searchBackground = Color(0xFF23242A),
    searchHint = Color(0xFFB9B9B9),
    searchIcon = Color(0xFFE2E1E5),
    conversationListSelected = Color(0xFF2C2F37),
    conversationListArchiveStart = Color(0xFFB6C5FA),
    conversationListArchiveEnd = Color(0xFFB6C5FA),
    conversationListUnread = Color(0xFFCE4A40),
    navigationBar = Color(0xFF1B1C1F),
    navigationBarScrim = Color(0x33FFFFFF),
    navigationItemActive = Color(0xFFB6C5FA),
    navigationItemInactive = Color(0xFF5E5E5E),
    bottomSheetBackground = Color(0xFF23242A),
    bottomSheetScrim = Color(0x14FFFFFF),
    bottomSheetNavigationBar = Color(0xFF1B1C1F),
    snackbarBackground = Color(0xFF303133),
    snackbarForeground = Color(0xFFBEBFC5),
    snackbarAction = Color(0xFFB6C5FA),
    toastBackground = Color(0xFF23242A),
    toastForeground = Color(0xFFE2E1E5),
    tooltipBackground = Color(0xFF414659),
    tooltipForeground = Color(0xFFE2E1E5),
    composerBackground = Color(0xFF1B1C1F),
    composerDivider = Color(0x32FFFFFF),
    composerText = Color(0xFFE2E1E5),
    composerHint = Color(0xFFB9B9B9),
    composerAttachButton = Color(0xFFBEBFC5),
    composerSendButton = Color(0xFFB6C5FA),
    composerMediaKeyboardBg = Color(0xFF23242A),
    composerMediaKeyboardBarBg = Color(0xFF31343E),
    mediaPreviewBarBackground = Color(0x90010101),
    mediaOverviewToolbarBackground = Color(0xFF1B1C1F),
    mediaOverviewToolbarSecondary = Color(0xFF23242A),
    mediaOverviewToolbarForeground = Color(0xFFFFFFFF),
    mediaOverviewHeaderForeground = Color(0xFF777777),
    cameraIconBackground = Color(0xFF3B3B3B),
    contactCheckboxBackground = Color(0xFF1B1C1F),
    contactFilterToolbarIcon = Color(0xFFBEBFC5),
    wallpaperPreviewBackground = Color(0x99000000),
    wallpaperComposeBackground = Color(0xB8000000),
    wallpaperBubble = Color(0xF51A1A1A),
    wallpaperNavigation = Color(0x80000000),
    storyBullet = Color(0xFF5E5E5E),
    storyGradientStart = Color(0x99000000),
    storyCaptionGradientStart = Color(0xCC000000),
    paymentCardOverflow = Color(0xFF8E94A7),
    paymentCurrencyCode = Color(0xFF8E94A7),
    insightTitle = Color(0xFFB9B9B9),
    insightBody = Color(0xFF5E5E5E),
    insightProgressBackground = Color(0xFF5E5E5E),
    qrCardColor = Color(0xFFE2E1E5),
    qrCardTextColor = Color(0xFFE7EBF3),
    releaseNotesBubble = Color(0xFF444664),
    releaseNotesBubbleText = Color(0xFFFFFFFF),
    releaseNotesCtaBackground = Color(0xFF636583),
    releaseNotesBackgroundPattern = Color(0xFF272C3C),
    releaseNotesHeaderBorder = Color(0xFF2E3342),
    releaseNotesBackground = Color(0xFF353A49),
    releaseNotesHeaderBackground = Color(0xFF3A3F4E),
    releaseNotesToolbarScrolled = Color(0xFF424757),
    releaseNotesToolbarTransparent = Color(0x00424757),
    megaphoneBackground = Color(0xFF262626),
    megaphoneBodyText = Color(0xFFBEBFC5),
    voiceNotePlayerBackground = Color(0xFF23242A),
    numPadKeyBackground = Color(0x33FFFFFF),
    attachmentKeyboardForeground = Color(0xFFE9E9E9),
    loginTopBackground = Color(0xFF000000),
    groupStoryIndicator = Color(0xFF6191F3),
    reactWithAnyBackground = Color(0xFF23242A),
    reactWithAnySearchBackground = Color(0xFF303133),
    reactWithAnySearchHint = Color(0xFFBEBFC5),
    reactWithAnyCustomizeBackground = Color(0xFF303133),
    settingsRipple = Color(0x18FFFFFF),
    lowPriorityButtonText = Color(0xFF8E94A7)
  ),
  transparency = TransparencyColors(
    transparent = Color(0x00000000),
    overlay5 = Color(0x0AFFFFFF),
    overlay10 = Color(0x18FFFFFF),
    overlay15 = Color(0x26FFFFFF),
    overlay20 = Color(0x33FFFFFF),
    overlay25 = Color(0x40FFFFFF),
    overlay30 = Color(0x4DFFFFFF),
    overlay40 = Color(0x66FFFFFF),
    overlay50 = Color(0x80FFFFFF),
    overlay60 = Color(0x99FFFFFF),
    overlay70 = Color(0xB2FFFFFF),
    overlay80 = Color(0xCCFFFFFF),
    inverseTransparent = Color(0x00000000),
    inverseOverlay5 = Color(0x0A000000),
    inverseOverlay10 = Color(0x18000000),
    inverseOverlay15 = Color(0x26000000),
    inverseOverlay20 = Color(0x33000000),
    inverseOverlay25 = Color(0x40000000),
    inverseOverlay30 = Color(0x4D000000),
    inverseOverlay40 = Color(0x66000000),
    inverseOverlay50 = Color(0x80000000),
    inverseOverlay60 = Color(0x99000000),
    inverseOverlay70 = Color(0xB3000000),
    inverseOverlay80 = Color(0xCC000000)
  ),
  conversation = ConversationThemeColors(
    outgoingBodyColor = Color(0xFFE2E1E5),
    outgoingFooterColor = Color(0xA3FFFFFF),
    sendButtonTint = Color(0xFFE2E1E5),
    scrollForegoundColor = Color(0xFFBEBFC5),
    typingIndicatorForeground = Color(0xFFBEBFC5),
    typingIndicatorForegroundWallpaper = Color(0xFFA3FFFFFF)
  ),
  isDarkMode = true
)