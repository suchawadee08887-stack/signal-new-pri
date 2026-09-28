/*
 * Copyright 2025 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.core.ui.compose.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Comprehensive interactive state token system.
 *
 * Defines visual states for all interactive elements:
 * - **Default**: Normal resting state
 * - **Hover**: Mouse hover (desktop/web)
 * - **Focus**: Keyboard focus ring
 * - **Pressed**: Touch/click active
 * - **Disabled**: Non-interactive state
 * - **Selected**: Toggle/selection state
 * - **Error**: Validation error state
 * - **Success**: Confirmation state
 */
@Immutable
data class SignalInteractiveStates(
  // ===== Button States =====
  val buttonDefault: ButtonState,
  val buttonPressed: ButtonState,
  val buttonDisabled: ButtonState,
  val buttonHovered: ButtonState,
  val buttonFocused: ButtonState,
  val buttonLoading: ButtonState,

  // ===== Icon Button States =====
  val iconButtonDefault: ButtonState,
  val iconButtonPressed: ButtonState,
  val iconButtonDisabled: ButtonState,
  val iconButtonHovered: ButtonState,

  // ===== FAB States =====
  val fabDefault: ButtonState,
  val fabPressed: ButtonState,
  val fabDisabled: ButtonState,

  // ===== Toggle States =====
  val switchOn: ToggleState,
  val switchOff: ToggleState,
  val switchDisabled: ToggleState,

  val checkboxChecked: ToggleState,
  val checkboxUnchecked: ToggleState,
  val checkboxDisabled: ToggleState,

  val radioSelected: ToggleState,
  val radioUnselected: ToggleState,
  val radioDisabled: ToggleState,

  // ===== Input States =====
  val inputDefault: InputState,
  val inputFocused: InputState,
  val inputError: InputState,
  val inputDisabled: InputState,
  val inputSuccess: InputState,

  // ===== List Item States =====
  val listItemDefault: ListItemState,
  val listItemHovered: ListItemState,
  val listItemPressed: ListItemState,
  val listItemSelected: ListItemState,
  val listItemDisabled: ListItemState,
  val listItemActive: ListItemState,

  // ===== Tab States =====
  val tabSelected: TabState,
  val tabUnselected: TabState,
  val tabDisabled: TabState,

  // ===== Chip States =====
  val chipDefault: ChipState,
  val chipSelected: ChipState,
  val chipDisabled: ChipState,
  val chipHovered: ChipState,

  // ===== Link States =====
  val linkDefault: LinkState,
  val linkHovered: LinkState,
  val linkPressed: LinkState,
  val linkVisited: LinkState,

  // ===== Bottom Sheet States =====
  val bottomSheetDraggable: BottomSheetState,
  val bottomSheetNotDraggable: BottomSheetState,

  // ===== Message States =====
  val messageDefault: MessageState,
  val messageHovered: MessageState,
  val messagePressed: MessageState,
  val messageSelected: MessageState,
  val messageDragged: MessageState,

  // ===== Search States =====
  val searchDefault: SearchState,
  val searchFocused: SearchState,
  val searchActive: SearchState,

  // ===== Avatar States =====
  val avatarDefault: AvatarState,
  val avatarPressed: AvatarState,
  val avatarSelected: AvatarState,
  val avatarDisabled: AvatarState,

  // ===== Story States =====
  val storyDefault: StoryState,
  val storyViewed: StoryState,
  val storyUnviewed: StoryState,

  // ===== Notification States =====
  val notificationDefault: NotificationState,
  val notificationRead: NotificationState,
  val notificationUnread: NotificationState
)

/** State for buttons (background, text, border). */
@Immutable
data class ButtonState(
  val background: Color,
  val foreground: Color,
  val border: Color,
  val ripple: Color,
  val elevation: androidx.compose.ui.unit.Dp = 0.dp
)

/** State for toggle controls. */
@Immutable
data class ToggleState(
  val track: Color,
  val thumb: Color,
  val checkmark: Color,
  val border: Color
)

/** State for input fields. */
@Immutable
data class InputState(
  val background: Color,
  val text: Color,
  val border: Color,
  val placeholder: Color,
  val cursor: Color,
  val highlight: Color
)

/** State for list items. */
@Immutable
data class ListItemState(
  val background: Color,
  val foreground: Color,
  val icon: Color,
  val selectedBackground: Color,
  val ripple: Color
)

/** State for tabs. */
@Immutable
data class TabState(
  val background: Color,
  val text: Color,
  val icon: Color,
  val indicator: Color,
  val ripple: Color
)

/** State for chips. */
@Immutable
data class ChipState(
  val background: Color,
  val foreground: Color,
  val border: Color,
  val icon: Color,
  val ripple: Color
)

/** State for links. */
@Immutable
data class LinkState(
  val text: Color,
  val underline: Color,
  val visited: Color
)

/** State for bottom sheets. */
@Immutable
data class BottomSheetState(
  val background: Color,
  val handle: Color,
  val scrim: Color
)

/** State for messages. */
@Immutable
data class MessageState(
  val background: Color,
  val foreground: Color,
  val overlay: Color,
  val selection: Color
)

/** State for search. */
@Immutable
data class SearchState(
  val background: Color,
  val text: Color,
  val placeholder: Color,
  val icon: Color
)

/** State for avatars. */
@Immutable
data class AvatarState(
  val background: Color,
  val foreground: Color,
  val border: Color,
  val ring: Color
)

/** State for stories. */
@Immutable
data class StoryState(
  val ring: Color,
  val background: Color,
  val foreground: Color
)

/** State for notifications. */
@Immutable
data class NotificationState(
  val background: Color,
  val foreground: Color,
  val badge: Color,
  val badgeText: Color
)

val LocalSignalInteractiveStates = staticCompositionLocalOf {
  signalInteractiveStates()
}

/** Create the default Signal interactive states. */
internal fun signalInteractiveStates(scheme: SignalColorScheme = createLightColorScheme()): SignalInteractiveStates {
  val brand = scheme.brand
  val surface = scheme.surface
  val text = scheme.text
  val interactive = scheme.interactive
  val status = scheme.status
  val border = scheme.border

  return SignalInteractiveStates(
    buttonDefault = ButtonState(
      background = interactive.buttonPrimary,
      foreground = interactive.buttonPrimaryText,
      border = Color.Transparent,
      ripple = interactive.buttonPrimaryRipple
    ),
    buttonPressed = ButtonState(
      background = interactive.buttonPrimaryPressed,
      foreground = interactive.buttonPrimaryText,
      border = Color.Transparent,
      ripple = interactive.buttonPrimaryRipple
    ),
    buttonDisabled = ButtonState(
      background = interactive.buttonPrimaryDisabled,
      foreground = interactive.buttonPrimaryTextDisabled,
      border = Color.Transparent,
      ripple = Color.Transparent
    ),
    buttonHovered = ButtonState(
      background = interactive.buttonPrimary,
      foreground = interactive.buttonPrimaryText,
      border = Color.Transparent,
      ripple = interactive.buttonPrimaryRipple
    ),
    buttonFocused = ButtonState(
      background = interactive.buttonPrimary,
      foreground = interactive.buttonPrimaryText,
      border = interactive.focusRing,
      ripple = interactive.buttonPrimaryRipple
    ),
    buttonLoading = ButtonState(
      background = interactive.buttonPrimaryDisabled,
      foreground = interactive.buttonPrimaryTextDisabled,
      border = Color.Transparent,
      ripple = Color.Transparent
    ),
    iconButtonDefault = ButtonState(
      background = Color.Transparent,
      foreground = text.primary,
      border = Color.Transparent,
      ripple = interactive.ripple
    ),
    iconButtonPressed = ButtonState(
      background = interactive.pressed,
      foreground = text.primary,
      border = Color.Transparent,
      ripple = interactive.ripple
    ),
    iconButtonDisabled = ButtonState(
      background = Color.Transparent,
      foreground = text.primaryDisabled,
      border = Color.Transparent,
      ripple = Color.Transparent
    ),
    iconButtonHovered = ButtonState(
      background = interactive.hover,
      foreground = text.primary,
      border = Color.Transparent,
      ripple = interactive.ripple
    ),
    fabDefault = ButtonState(
      background = interactive.buttonPrimary,
      foreground = interactive.buttonPrimaryText,
      border = Color.Transparent,
      ripple = interactive.buttonPrimaryRipple,
      elevation = 4.dp
    ),
    fabPressed = ButtonState(
      background = interactive.buttonPrimaryPressed,
      foreground = interactive.buttonPrimaryText,
      border = Color.Transparent,
      ripple = interactive.buttonPrimaryRipple,
      elevation = 8.dp
    ),
    fabDisabled = ButtonState(
      background = interactive.buttonPrimaryDisabled,
      foreground = interactive.buttonPrimaryTextDisabled,
      border = Color.Transparent,
      ripple = Color.Transparent,
      elevation = 4.dp
    ),
    switchOn = ToggleState(
      track = interactive.switch,
      thumb = interactive.buttonPrimaryText,
      checkmark = interactive.buttonPrimaryText,
      border = Color.Transparent
    ),
    switchOff = ToggleState(
      track = interactive.buttonSecondary,
      thumb = text.secondary,
      checkmark = Color.Transparent,
      border = Color.Transparent
    ),
    switchDisabled = ToggleState(
      track = interactive.buttonSecondaryDisabled,
      thumb = text.primaryDisabled,
      checkmark = Color.Transparent,
      border = Color.Transparent
    ),
    checkboxChecked = ToggleState(
      track = interactive.checkbox,
      thumb = text.primary,
      checkmark = interactive.buttonPrimaryText,
      border = Color.Transparent
    ),
    checkboxUnchecked = ToggleState(
      track = Color.Transparent,
      thumb = text.secondary,
      checkmark = Color.Transparent,
      border = border.border
    ),
    checkboxDisabled = ToggleState(
      track = interactive.checkboxDisabled,
      thumb = text.primaryDisabled,
      checkmark = Color.Transparent,
      border = border.border
    ),
    radioSelected = ToggleState(
      track = interactive.radio,
      thumb = text.primary,
      checkmark = interactive.buttonPrimaryText,
      border = Color.Transparent
    ),
    radioUnselected = ToggleState(
      track = Color.Transparent,
      thumb = text.secondary,
      checkmark = Color.Transparent,
      border = border.border
    ),
    radioDisabled = ToggleState(
      track = interactive.radioDisabled,
      thumb = text.primaryDisabled,
      checkmark = Color.Transparent,
      border = border.border
    ),
    inputDefault = InputState(
      background = surface.surface1,
      text = text.primary,
      border = border.outline,
      placeholder = text.hint,
      cursor = interactive.textCursor,
      highlight = interactive.textHighlight
    ),
    inputFocused = InputState(
      background = surface.surface1,
      text = text.primary,
      border = interactive.focusRing,
      placeholder = text.hint,
      cursor = interactive.textCursor,
      highlight = interactive.textHighlight
    ),
    inputError = InputState(
      background = surface.surface1,
      text = text.primary,
      border = border.borderError,
      placeholder = text.hint,
      cursor = interactive.textCursor,
      highlight = interactive.textHighlight
    ),
    inputDisabled = InputState(
      background = surface.surface2,
      text = text.primaryDisabled,
      border = border.outline,
      placeholder = text.primaryDisabled,
      cursor = Color.Transparent,
      highlight = Color.Transparent
    ),
    inputSuccess = InputState(
      background = surface.surface1,
      text = text.primary,
      border = status.success,
      placeholder = text.hint,
      cursor = interactive.textCursor,
      highlight = interactive.textHighlight
    ),
    listItemDefault = ListItemState(
      background = Color.Transparent,
      foreground = text.primary,
      icon = text.secondary,
      selectedBackground = interactive.selected,
      ripple = interactive.ripple
    ),
    listItemHovered = ListItemState(
      background = interactive.hoverOverlay,
      foreground = text.primary,
      icon = text.secondary,
      selectedBackground = interactive.selected,
      ripple = interactive.ripple
    ),
    listItemPressed = ListItemState(
      background = interactive.pressedOverlay,
      foreground = text.primary,
      icon = text.secondary,
      selectedBackground = interactive.selected,
      ripple = interactive.ripple
    ),
    listItemSelected = ListItemState(
      background = interactive.selectedOverlay,
      foreground = text.primary,
      icon = text.secondary,
      selectedBackground = interactive.selected,
      ripple = interactive.ripple
    ),
    listItemDisabled = ListItemState(
      background = Color.Transparent,
      foreground = text.primaryDisabled,
      icon = text.primaryDisabled,
      selectedBackground = interactive.selectedOverlay,
      ripple = Color.Transparent
    ),
    listItemActive = ListItemState(
      background = interactive.selected,
      foreground = brand.primary,
      icon = brand.primary,
      selectedBackground = interactive.selected,
      ripple = interactive.ripple
    ),
    tabSelected = TabState(
      background = Color.Transparent,
      text = brand.primary,
      icon = brand.primary,
      indicator = brand.primary,
      ripple = interactive.ripple
    ),
    tabUnselected = TabState(
      background = Color.Transparent,
      text = text.secondary,
      icon = text.secondary,
      indicator = Color.Transparent,
      ripple = interactive.ripple
    ),
    tabDisabled = TabState(
      background = Color.Transparent,
      text = text.primaryDisabled,
      icon = text.primaryDisabled,
      indicator = Color.Transparent,
      ripple = Color.Transparent
    ),
    chipDefault = ChipState(
      background = surface.surfaceVariant,
      foreground = text.primary,
      border = Color.Transparent,
      icon = text.secondary,
      ripple = interactive.ripple
    ),
    chipSelected = ChipState(
      background = interactive.selected,
      foreground = brand.primary,
      border = Color.Transparent,
      icon = brand.primary,
      ripple = interactive.ripple
    ),
    chipDisabled = ChipState(
      background = surface.surface2,
      foreground = text.primaryDisabled,
      border = Color.Transparent,
      icon = text.primaryDisabled,
      ripple = Color.Transparent
    ),
    chipHovered = ChipState(
      background = surface.surface3,
      foreground = text.primary,
      border = Color.Transparent,
      icon = text.secondary,
      ripple = interactive.ripple
    ),
    linkDefault = LinkState(
      text = brand.accent,
      underline = brand.accent,
      visited = brand.accentDark
    ),
    linkHovered = LinkState(
      text = brand.accentLight,
      underline = brand.accentLight,
      visited = brand.accentDark
    ),
    linkPressed = LinkState(
      text = brand.accentDark,
      underline = brand.accentDark,
      visited = brand.accentDark
    ),
    linkVisited = LinkState(
      text = brand.accentDark,
      underline = brand.accentDark,
      visited = brand.accentDark
    ),
    bottomSheetDraggable = BottomSheetState(
      background = surface.surface1,
      handle = text.secondary,
      scrim = surface.sheetScrim
    ),
    bottomSheetNotDraggable = BottomSheetState(
      background = surface.surface1,
      handle = Color.Transparent,
      scrim = surface.sheetScrim
    ),
    messageDefault = MessageState(
      background = Color.Transparent,
      foreground = text.primary,
      overlay = Color.Transparent,
      selection = interactive.selected
    ),
    messageHovered = MessageState(
      background = interactive.hoverOverlay,
      foreground = text.primary,
      overlay = interactive.hoverOverlay,
      selection = interactive.selected
    ),
    messagePressed = MessageState(
      background = interactive.pressedOverlay,
      foreground = text.primary,
      overlay = interactive.pressedOverlay,
      selection = interactive.selected
    ),
    messageSelected = MessageState(
      background = interactive.selectedOverlay,
      foreground = text.primary,
      overlay = interactive.selectedOverlay,
      selection = interactive.selected
    ),
    messageDragged = MessageState(
      background = interactive.selectedOverlay,
      foreground = text.primary,
      overlay = interactive.selectedOverlay,
      selection = interactive.selected
    ),
    searchDefault = SearchState(
      background = surface.surface2,
      text = text.primary,
      placeholder = text.hint,
      icon = text.secondary
    ),
    searchFocused = SearchState(
      background = surface.surface2,
      text = text.primary,
      placeholder = text.hint,
      icon = brand.primary
    ),
    searchActive = SearchState(
      background = surface.surface2,
      text = text.primary,
      placeholder = text.hint,
      icon = brand.primary
    ),
    avatarDefault = AvatarState(
      background = surface.surface2,
      foreground = text.secondary,
      border = Color.Transparent,
      ring = Color.Transparent
    ),
    avatarPressed = AvatarState(
      background = surface.surface3,
      foreground = text.secondary,
      border = Color.Transparent,
      ring = Color.Transparent
    ),
    avatarSelected = AvatarState(
      background = surface.surface2,
      foreground = text.secondary,
      border = brand.primary,
      ring = brand.primary
    ),
    avatarDisabled = AvatarState(
      background = surface.surface1,
      foreground = text.primaryDisabled,
      border = Color.Transparent,
      ring = Color.Transparent
    ),
    storyDefault = StoryState(
      ring = surface.surfaceVariant,
      background = surface.surface2,
      foreground = text.secondary
    ),
    storyViewed = StoryState(
      ring = surface.surfaceVariant,
      background = surface.surface2,
      foreground = text.secondary
    ),
    storyUnviewed = StoryState(
      ring = brand.primary,
      background = surface.surface2,
      foreground = text.secondary
    ),
    notificationDefault = NotificationState(
      background = surface.surface1,
      foreground = text.primary,
      badge = status.unreadBadge,
      badgeText = status.unreadBadgeText
    ),
    notificationRead = NotificationState(
      background = surface.surface1,
      foreground = text.secondary,
      badge = Color.Transparent,
      badgeText = Color.Transparent
    ),
    notificationUnread = NotificationState(
      background = surface.surface1,
      foreground = text.primary,
      badge = status.unreadBadge,
      badgeText = status.unreadBadgeText
    )
  )
}