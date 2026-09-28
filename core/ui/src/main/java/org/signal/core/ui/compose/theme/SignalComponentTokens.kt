/*
 * Copyright 2025 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.core.ui.compose.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Component-specific token system for messaging interfaces.
 *
 * Maps semantic tokens to specific UI components used across the messaging application.
 * Each component token references the base tokens from the color, spacing, shape, and other
 * token systems, providing a clear mapping between design tokens and component usage.
 *
 * Components covered:
 * - Conversation / Chat List
 * - Message Bubbles / Content
 * - Composer / Input
 * - Attachments / Media
 * - Contacts / Profiles
 * - Notifications
 * - Search
 * - Dialogs / Menus
 * - Settings
 * - Calls / WebRTC
 * - Stories
 * - Backup / Registration
 */
@Immutable
data class SignalComponentTokens(
  val conversation: ConversationComponentTokens,
  val chatList: ChatListComponentTokens,
  val composer: ComposerComponentTokens,
  val attachments: AttachmentComponentTokens,
  val mediaPreview: MediaPreviewComponentTokens,
  val contacts: ContactComponentTokens,
  val profile: ProfileComponentTokens,
  val notifications: NotificationComponentTokens,
  val search: SearchComponentTokens,
  val dialogs: DialogComponentTokens,
  val menus: MenuComponentTokens,
  val settings: SettingsComponentTokens,
  val calls: CallComponentTokens,
  val stories: StoryComponentTokens,
  val backup: BackupComponentTokens,
  val registration: RegistrationComponentTokens
)

// ============================================================================
// Conversation / Chat List
// ============================================================================

@Immutable
data class ConversationComponentTokens(
  // Toolbar
  val toolbarBackground: Color,
  val toolbarBackgroundScrolled: Color,
  val toolbarBackgroundWallpaper: Color,
  val toolbarBackgroundWallpaperScrolled: Color,
  val toolbarBackgroundIncognito: Color,
  val toolbarForeground: Color,
  val toolbarSubtitle: Color,
  val toolbarHeight: Dp,
  val toolbarIconSize: Dp,
  val toolbarPaddingHorizontal: Dp,
  val toolbarPaddingVertical: Dp,
  val toolbarAvatarSize: Dp,

  // Header
  val headerHeight: Dp,
  val headerPadding: Dp,

  // Date separator
  val dateSeparatorColor: Color,
  val dateSeparatorTextColor: Color,
  val dateSeparatorSize: Dp,

  // Scroll to bottom
  val scrollButtonBackground: Color,
  val scrollButtonForeground: Color,
  val scrollButtonSize: Dp,
  val scrollButtonSpacing: Dp,
  val scrollButtonPadding: Dp,

  // Empty state
  val emptyStateBackground: Color,
  val emptyStateTextColor: Color,
  val emptyStateIconColor: Color
)

// ============================================================================
// Chat List
// ============================================================================

@Immutable
data class ChatListComponentTokens(
  // Item
  val itemBackground: Color,
  val itemBackgroundSelected: Color,
  val itemBackgroundPressed: Color,
  val itemPaddingHorizontal: Dp,
  val itemPaddingVertical: Dp,
  val itemGap: Dp,
  val itemHeight: Dp,

  // Avatar
  val avatarSize: Dp,
  val avatarRadius: Dp,

  // Name
  val nameColor: Color,
  val nameColorUnread: Color,
  val nameStyle: Int,

  // Message preview
  val previewColor: Color,
  val previewColorUnread: Color,

  // Timestamp
  val timestampColor: Color,
  val timestampColorUnread: Color,

  // Unread badge
  val unreadBadgeColor: Color,
  val unreadBadgeTextColor: Color,
  val unreadBadgeSize: Dp,
  val unreadBadgeRadius: Dp,

  // Online indicator
  val onlineIndicatorColor: Color,
  val onlineIndicatorSize: Dp,

  // Archived icon
  val archivedIconColor: Color,
  val archivedIconSize: Dp,

  // Separator
  val separatorColor: Color,
  val separatorHeight: Dp,
  val separatorInset: Dp,

  // Archive swipe
  val archiveBackgroundStart: Color,
  val archiveBackgroundEnd: Color,

  // Pinned
  val pinnedIconColor: Color,
  val pinnedIconSize: Dp
)

// ============================================================================
// Composer / Input
// ============================================================================

@Immutable
data class ComposerComponentTokens(
  // Container
  val background: Color,
  val height: Dp,
  val paddingHorizontal: Dp,
  val paddingVertical: Dp,

  // Text input
  val textColor: Color,
  val textHintColor: Color,
  val textSize: Dp,

  // Divider
  val dividerColor: Color,
  val dividerHeight: Dp,

  // Buttons
  val attachButtonColor: Color,
  val sendButtonColor: Color,
  val sendButtonSize: Dp,
  val emojiButtonColor: Color,

  // Voice note
  val voiceNoteBackground: Color,
  val voiceNoteRecording: Color,
  val voiceNoteTimer: Color,

  // Media keyboard
  val mediaKeyboardBackground: Color,
  val mediaKeyboardBarBackground: Color,
  val mediaKeyboardTabActive: Color,
  val mediaKeyboardTabInactive: Color,

  // Sticker/emoji
  val stickerPanelBackground: Color,
  val emojiPanelBackground: Color
)

// ============================================================================
// Attachments / Media
// ============================================================================

@Immutable
data class AttachmentComponentTokens(
  // Container
  val background: Color,
  val foreground: Color,
  val borderColor: Color,
  val borderWidth: Dp,
  val borderRadius: Dp,

  // Thumbnail
  val thumbnailBackground: Color,
  val thumbnailRadius: Dp,
  val thumbnailOverlay: Color,
  val thumbnailBorder: Color,

  // File
  val fileIconColor: Color,
  val fileNameColor: Color,
  val fileSizeColor: Color,
  val fileBackground: Color,

  // Image
  val imageOverlay: Color,
  val imagePlaceholder: Color,

  // Document
  val docTitleColor: Color,
  val docCaptionColor: Color,
  val docDownloadButtonTint: Color,

  // Link preview
  val linkPreviewBackground: Color,
  val linkPreviewBorderColor: Color,
  val linkPreviewTitleColor: Color,
  val linkPreviewDescColor: Color,
  val linkPreviewDomainColor: Color,

  // Quote / Reply
  val quoteBackground: Color,
  val quoteBarColor: Color,
  val quoteTextColor: Color,
  val quoteLabelBackground: Color,
  val quoteMissingIconColor: Color
)

// ============================================================================
// Media Preview
// ============================================================================

@Immutable
data class MediaPreviewComponentTokens(
  val barBackground: Color,
  val barHeight: Dp,
  val barPadding: Dp,
  val barForeground: Color,
  val barOverlay: Color,
  val thumbSize: Dp,
  val thumbRadius: Dp,
  val thumbBorder: Color,
  val thumbBorderWidth: Dp,
  val selectedThumbBorder: Color,
  val selectedThumbBorderWidth: Dp,
  val itemCornerRadius: Dp,
  val itemSpacing: Dp,
  val overlayColor: Color
)

// ============================================================================
// Contacts
// ============================================================================

@Immutable
data class ContactComponentTokens(
  val nameColor: Color,
  val subtitleColor: Color,
  val detailColor: Color,
  val iconColor: Color,
  val avatarSize: Dp,
  val avatarRadius: Dp,
  val itemHeight: Dp,
  val itemPadding: Dp,
  val itemGap: Dp,
  val checkboxBackground: Color,
  val filterIconColor: Color,
  val sectionHeaderColor: Color
)

// ============================================================================
// Profile
// ============================================================================

@Immutable
data class ProfileComponentTokens(
  val nameColor: Color,
  val nameStyle: Int,
  val bioColor: Color,
  val statusColor: Color,
  val avatarSize: Dp,
  val avatarRadius: Dp,
  val sectionSpacing: Dp,
  val sectionHeaderColor: Color,
  val actionButtonColor: Color,
  val actionButtonBackground: Color
)

// ============================================================================
// Notifications
// ============================================================================

@Immutable
data class NotificationComponentTokens(
  val background: Color,
  val foreground: Color,
  val badgeColor: Color,
  val badgeTextColor: Color,
  val titleColor: Color,
  val bodyColor: Color,
  val timestampColor: Color,
  val iconColor: Color,
  val soundColor: Color,
  val vibrateColor: Color
)

// ============================================================================
// Search
// ============================================================================

@Immutable
data class SearchComponentTokens(
  val backgroundColor: Color,
  val textColor: Color,
  val hintColor: Color,
  val iconColor: Color,
  val cursorColor: Color,
  val resultTitleColor: Color,
  val resultSnippetColor: Color,
  val resultHighlightColor: Color,
  val resultBackground: Color,
  val resultSelectedBackground: Color,
  val resultBorder: Color,
  val resultDividerColor: Color,
  val resultAvatarSize: Dp,
  val resultHeight: Dp,
  val resultPadding: Dp,
  val searchBarHeight: Dp,
  val searchBarRadius: Dp,
  val tabBarHeight: Dp,
  val tabActiveColor: Color,
  val tabInactiveColor: Color
)

// ============================================================================
// Dialogs
// ============================================================================

@Immutable
data class DialogComponentTokens(
  val background: Color,
  val foreground: Color,
  val titleColor: Color,
  val bodyColor: Color,
  val actionColor: Color,
  val actionTextColor: Color,
  val dividerColor: Color,
  val borderRadius: Dp,
  val paddingHorizontal: Dp,
  val paddingVertical: Dp,
  val scrimColor: Color
)

// ============================================================================
// Menus
// ============================================================================

@Immutable
data class MenuComponentTokens(
  val background: Color,
  val foreground: Color,
  val iconColor: Color,
  val dividerColor: Color,
  val itemHeight: Dp,
  val itemPadding: Dp,
  val borderRadius: Dp,
  val elevation: Dp,
  val scrimColor: Color
)

// ============================================================================
// Settings
// ============================================================================

@Immutable
data class SettingsComponentTokens(
  val background: Color,
  val surface: Color,
  val surfaceVariant: Color,
  val textColor: Color,
  val subtitleColor: Color,
  val descriptionColor: Color,
  val sectionHeaderColor: Color,
  val sectionHeaderBackground: Color,
  val iconColor: Color,
  val rippleColor: Color,
  val dividerColor: Color,
  val itemHeight: Dp,
  val itemPadding: Dp,
  val sectionGap: Dp,
  val toolbarBackground: Color,
  val toolbarForeground: Color
)

// ============================================================================
// Calls / WebRTC
// ============================================================================

@Immutable
data class CallComponentTokens(
  val background: Color,
  val foreground: Color,
  val iconColor: Color,
  val hangupColor: Color,
  val answerColor: Color,
  val actionButtonColor: Color,
  val actionButtonBackground: Color,
  val nameColor: Color,
  val statusColor: Color,
  val timerColor: Color,
  val controlsBackground: Color,
  val controlsForeground: Color,
  val controlsIconColor: Color,
  val snackbarBackground: Color,
  val snackbarForeground: Color
)

// ============================================================================
// Stories
// ============================================================================

@Immutable
data class StoryComponentTokens(
  val background: Color,
  val foreground: Color,
  val ringColor: Color,
  val ringColorViewed: Color,
  val ringColorUnviewed: Color,
  val bulletColor: Color,
  val gradientStart: Color,
  val captionGradientStart: Color,
  val textOverlayColor: Color,
  val textOverlaySize: Dp,
  val previewCornerRadius: Dp,
  val previewWidth: Dp,
  val previewHeight: Dp
)

// ============================================================================
// Backup / Registration
// ============================================================================

@Immutable
data class BackupComponentTokens(
  val background: Color,
  val foreground: Color,
  val titleColor: Color,
  val bodyColor: Color,
  val labelColor: Color,
  val iconColor: Color,
  val actionButtonColor: Color,
  val actionButtonBackground: Color,
  val progressBarColor: Color,
  val progressBarBackground: Color
)

@Immutable
data class RegistrationComponentTokens(
  val background: Color,
  val foreground: Color,
  val titleColor: Color,
  val bodyColor: Color,
  val inputBackground: Color,
  val inputTextColor: Color,
  val inputHintColor: Color,
  val inputBorderColor: Color,
  val inputFocusBorderColor: Color,
  val inputErrorBorderColor: Color,
  val buttonColor: Color,
  val buttonTextColor: Color,
  val buttonDisabledColor: Color,
  val buttonDisabledTextColor: Color,
  val topBackground: Color,
  val topBackgroundLight: Color,
  val topBackgroundDark: Color,
  val illustrationColor: Color
)

// ============================================================================
// CompositionLocal
// ============================================================================

val LocalSignalComponentTokens = staticCompositionLocalOf {
  // Will be overridden by SignalTheme
  createDefaultComponentTokens()
}

/** Create the default component tokens. */
internal fun createDefaultComponentTokens(scheme: SignalColorScheme = createLightColorScheme()): SignalComponentTokens {
  return SignalComponentTokens(
    conversation = ConversationComponentTokens(
      toolbarBackground = scheme.component.toolbarBackground,
      toolbarBackgroundScrolled = scheme.component.toolbarBackgroundScrolled,
      toolbarBackgroundWallpaper = scheme.component.toolbarBackgroundWallpaper,
      toolbarBackgroundWallpaperScrolled = scheme.component.toolbarBackgroundWallpaperScrolled,
      toolbarBackgroundIncognito = scheme.component.toolbarBackgroundIncognito,
      toolbarForeground = scheme.component.toolbarForeground,
      toolbarSubtitle = scheme.component.toolbarSubtitle,
      toolbarHeight = 56.dp,
      toolbarIconSize = 24.dp,
      toolbarPaddingHorizontal = 16.dp,
      toolbarPaddingVertical = 8.dp,
      toolbarAvatarSize = 28.dp,
      headerHeight = 56.dp,
      headerPadding = 16.dp,
      dateSeparatorColor = scheme.surface.surface2,
      dateSeparatorTextColor = scheme.text.secondary,
      dateSeparatorSize = 28.dp,
      scrollButtonBackground = scheme.surface.background,
      scrollButtonForeground = scheme.conversation.scrollForegoundColor,
      scrollButtonSize = 48.dp,
      scrollButtonSpacing = 8.dp,
      scrollButtonPadding = 16.dp,
      emptyStateBackground = scheme.surface.background,
      emptyStateTextColor = scheme.text.secondary,
      emptyStateIconColor = scheme.text.secondary
    ),
    chatList = ChatListComponentTokens(
      itemBackground = scheme.surface.background,
      itemBackgroundSelected = scheme.component.conversationListSelected,
      itemBackgroundPressed = scheme.surface.surface1,
      itemPaddingHorizontal = 16.dp,
      itemPaddingVertical = 12.dp,
      itemGap = 8.dp,
      itemHeight = 72.dp,
      avatarSize = 52.dp,
      avatarRadius = 26.dp,
      nameColor = scheme.text.primary,
      nameColorUnread = scheme.text.primary,
      nameStyle = 0,
      previewColor = scheme.text.secondary,
      previewColorUnread = scheme.text.primary,
      timestampColor = scheme.text.secondary,
      timestampColorUnread = scheme.text.secondary,
      unreadBadgeColor = scheme.status.unreadBadge,
      unreadBadgeTextColor = scheme.status.unreadBadgeText,
      unreadBadgeSize = 16.dp,
      unreadBadgeRadius = 8.dp,
      onlineIndicatorColor = scheme.status.online,
      onlineIndicatorSize = 12.dp,
      archivedIconColor = scheme.icon.secondary,
      archivedIconSize = 24.dp,
      separatorColor = scheme.border.dividerMinor,
      separatorHeight = 1.dp,
      separatorInset = 72.dp,
      archiveBackgroundStart = scheme.component.conversationListArchiveStart,
      archiveBackgroundEnd = scheme.component.conversationListArchiveEnd,
      pinnedIconColor = scheme.icon.secondary,
      pinnedIconSize = 16.dp
    ),
    composer = ComposerComponentTokens(
      background = scheme.component.composerBackground,
      height = 48.dp,
      paddingHorizontal = 8.dp,
      paddingVertical = 4.dp,
      textColor = scheme.component.composerText,
      textHintColor = scheme.component.composerHint,
      textSize = 16.dp,
      dividerColor = scheme.component.composerDivider,
      dividerHeight = 1.dp,
      attachButtonColor = scheme.component.composerAttachButton,
      sendButtonColor = scheme.component.composerSendButton,
      sendButtonSize = 48.dp,
      emojiButtonColor = scheme.component.composerAttachButton,
      voiceNoteBackground = scheme.surface.surface1,
      voiceNoteRecording = scheme.status.error,
      voiceNoteTimer = scheme.text.secondary,
      mediaKeyboardBackground = scheme.component.composerMediaKeyboardBg,
      mediaKeyboardBarBackground = scheme.component.composerMediaKeyboardBarBg,
      mediaKeyboardTabActive = scheme.brand.primary,
      mediaKeyboardTabInactive = scheme.text.secondary,
      stickerPanelBackground = scheme.surface.surface1,
      emojiPanelBackground = scheme.surface.surface1
    ),
    attachments = AttachmentComponentTokens(
      background = scheme.surface.surface1,
      foreground = scheme.text.primary,
      borderColor = scheme.border.dividerMinor,
      borderWidth = 1.dp,
      borderRadius = 12.dp,
      thumbnailBackground = scheme.surface.surface2,
      thumbnailRadius = 12.dp,
      thumbnailOverlay = scheme.component.mediaPreviewBarBackground,
      thumbnailBorder = scheme.border.outlineVariant,
      fileIconColor = scheme.icon.primary,
      fileNameColor = scheme.text.primary,
      fileSizeColor = scheme.text.secondary,
      fileBackground = scheme.surface.surface1,
      imageOverlay = scheme.message.mediaThumbnailOverlay,
      imagePlaceholder = scheme.surface.surface2,
      docTitleColor = scheme.text.primary,
      docCaptionColor = scheme.text.secondary,
      docDownloadButtonTint = scheme.brand.primary,
      linkPreviewBackground = scheme.surface.surface1,
      linkPreviewBorderColor = scheme.border.dividerMinor,
      linkPreviewTitleColor = scheme.text.primary,
      linkPreviewDescColor = scheme.text.secondary,
      linkPreviewDomainColor = scheme.text.tertiary,
      quoteBackground = scheme.surface.surface1,
      quoteBarColor = scheme.brand.primary,
      quoteTextColor = scheme.text.primary,
      quoteLabelBackground = scheme.surface.surface2,
      quoteMissingIconColor = scheme.message.quoteMissingIcon
    ),
    mediaPreview = MediaPreviewComponentTokens(
      barBackground = scheme.component.mediaPreviewBarBackground,
      barHeight = 64.dp,
      barPadding = 8.dp,
      barForeground = scheme.text.inverse,
      barOverlay = scheme.component.mediaPreviewBarBackground,
      thumbSize = 64.dp,
      thumbRadius = 10.dp,
      thumbBorder = Color.Transparent,
      thumbBorderWidth = 0.dp,
      selectedThumbBorder = scheme.brand.primary,
      selectedThumbBorderWidth = 2.dp,
      itemCornerRadius = 10.dp,
      itemSpacing = 4.dp,
      overlayColor = scheme.component.mediaPreviewBarBackground
    ),
    contacts = ContactComponentTokens(
      nameColor = scheme.text.primary,
      subtitleColor = scheme.text.secondary,
      detailColor = scheme.text.tertiary,
      iconColor = scheme.icon.primary,
      avatarSize = 52.dp,
      avatarRadius = 26.dp,
      itemHeight = 72.dp,
      itemPadding = 16.dp,
      itemGap = 8.dp,
      checkboxBackground = scheme.component.contactCheckboxBackground,
      filterIconColor = scheme.component.contactFilterToolbarIcon,
      sectionHeaderColor = scheme.text.secondary
    ),
    profile = ProfileComponentTokens(
      nameColor = scheme.text.primary,
      nameStyle = 0,
      bioColor = scheme.text.secondary,
      statusColor = scheme.text.tertiary,
      avatarSize = 120.dp,
      avatarRadius = 60.dp,
      sectionSpacing = 24.dp,
      sectionHeaderColor = scheme.text.secondary,
      actionButtonColor = scheme.brand.primary,
      actionButtonBackground = scheme.brand.primaryContainer
    ),
    notifications = NotificationComponentTokens(
      background = scheme.surface.surface1,
      foreground = scheme.text.primary,
      badgeColor = scheme.status.unreadBadge,
      badgeTextColor = scheme.status.unreadBadgeText,
      titleColor = scheme.text.primary,
      bodyColor = scheme.text.secondary,
      timestampColor = scheme.text.tertiary,
      iconColor = scheme.icon.primary,
      soundColor = scheme.brand.primary,
      vibrateColor = scheme.brand.primary
    ),
    search = SearchComponentTokens(
      backgroundColor = scheme.component.searchBackground,
      textColor = scheme.text.primary,
      hintColor = scheme.component.searchHint,
      iconColor = scheme.component.searchIcon,
      cursorColor = scheme.interactive.textCursor,
      resultTitleColor = scheme.text.primary,
      resultSnippetColor = scheme.text.secondary,
      resultHighlightColor = scheme.interactive.textHighlight,
      resultBackground = Color.Transparent,
      resultSelectedBackground = scheme.interactive.selectedOverlay,
      resultBorder = scheme.border.dividerMinor,
      resultDividerColor = scheme.border.dividerMinor,
      resultAvatarSize = 52.dp,
      resultHeight = 72.dp,
      resultPadding = 16.dp,
      searchBarHeight = 40.dp,
      searchBarRadius = 999.dp,
      tabBarHeight = 48.dp,
      tabActiveColor = scheme.brand.primary,
      tabInactiveColor = scheme.text.secondary
    ),
    dialogs = DialogComponentTokens(
      background = scheme.surface.sheet,
      foreground = scheme.text.primary,
      titleColor = scheme.text.primary,
      bodyColor = scheme.text.secondary,
      actionColor = scheme.brand.primary,
      actionTextColor = scheme.brand.onPrimary,
      dividerColor = scheme.border.dividerMinor,
      borderRadius = 28.dp,
      paddingHorizontal = 24.dp,
      paddingVertical = 24.dp,
      scrimColor = scheme.surface.sheetScrim
    ),
    menus = MenuComponentTokens(
      background = scheme.surface.surface2,
      foreground = scheme.text.primary,
      iconColor = scheme.icon.secondary,
      dividerColor = scheme.border.dividerMinor,
      itemHeight = 48.dp,
      itemPadding = 16.dp,
      borderRadius = 12.dp,
      elevation = 4.dp,
      scrimColor = scheme.surface.sheetScrim
    ),
    settings = SettingsComponentTokens(
      background = scheme.surface.background,
      surface = scheme.surface.surface,
      surfaceVariant = scheme.surface.surfaceVariant,
      textColor = scheme.text.primary,
      subtitleColor = scheme.text.secondary,
      descriptionColor = scheme.text.tertiary,
      sectionHeaderColor = scheme.text.secondary,
      sectionHeaderBackground = scheme.surface.surface1,
      iconColor = scheme.icon.primary,
      rippleColor = scheme.component.settingsRipple,
      dividerColor = scheme.border.dividerMinor,
      itemHeight = 56.dp,
      itemPadding = 16.dp,
      sectionGap = 24.dp,
      toolbarBackground = scheme.component.toolbarBackground,
      toolbarForeground = scheme.text.primary
    ),
    calls = CallComponentTokens(
      background = scheme.surface.background,
      foreground = scheme.text.primary,
      iconColor = scheme.icon.primary,
      hangupColor = Color(0xFFF07168),
      answerColor = Color(0xFF34C759),
      actionButtonColor = scheme.brand.primary,
      actionButtonBackground = scheme.brand.primaryContainer,
      nameColor = scheme.text.primary,
      statusColor = scheme.text.secondary,
      timerColor = scheme.text.secondary,
      controlsBackground = scheme.surface.surface1,
      controlsForeground = scheme.text.primary,
      controlsIconColor = scheme.icon.primary,
      snackbarBackground = scheme.component.snackbarBackground,
      snackbarForeground = scheme.component.snackbarForeground
    ),
    stories = StoryComponentTokens(
      background = scheme.surface.background,
      foreground = scheme.text.primary,
      ringColor = scheme.surface.surfaceVariant,
      ringColorViewed = scheme.surface.surfaceVariant,
      ringColorUnviewed = scheme.brand.primary,
      bulletColor = scheme.component.storyBullet,
      gradientStart = scheme.component.storyGradientStart,
      captionGradientStart = scheme.component.storyCaptionGradientStart,
      textOverlayColor = scheme.text.inverse,
      textOverlaySize = 18.dp,
      previewCornerRadius = 10.dp,
      previewWidth = 100.dp,
      previewHeight = 140.dp
    ),
    backup = BackupComponentTokens(
      background = scheme.surface.background,
      foreground = scheme.text.primary,
      titleColor = scheme.text.primary,
      bodyColor = scheme.text.secondary,
      labelColor = scheme.text.tertiary,
      iconColor = scheme.icon.primary,
      actionButtonColor = scheme.brand.primary,
      actionButtonBackground = scheme.brand.primaryContainer,
      progressBarColor = scheme.brand.primary,
      progressBarBackground = scheme.surface.surfaceVariant
    ),
    registration = RegistrationComponentTokens(
      background = scheme.surface.background,
      foreground = scheme.text.primary,
      titleColor = scheme.text.primary,
      bodyColor = scheme.text.secondary,
      inputBackground = scheme.surface.surface1,
      inputTextColor = scheme.text.primary,
      inputHintColor = scheme.text.hint,
      inputBorderColor = scheme.border.outline,
      inputFocusBorderColor = scheme.interactive.focusRing,
      inputErrorBorderColor = scheme.border.borderError,
      buttonColor = scheme.brand.primary,
      buttonTextColor = scheme.brand.onPrimary,
      buttonDisabledColor = scheme.interactive.buttonPrimaryDisabled,
      buttonDisabledTextColor = scheme.interactive.buttonPrimaryTextDisabled,
      topBackground = scheme.brand.primary,
      topBackgroundLight = scheme.brand.primary,
      topBackgroundDark = scheme.brand.primaryDark,
      illustrationColor = scheme.brand.primary
    )
  )
}