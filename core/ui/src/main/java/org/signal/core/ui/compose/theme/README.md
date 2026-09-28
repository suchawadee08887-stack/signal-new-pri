# Signal Android Theme Management System

A comprehensive, centralized, and modular interface and theme management system for the Signal Android messaging application. This system standardizes all visual and UI settings across every screen and component, enabling global theme changes without duplicating configuration.

## Architecture

The theme system is built on top of Jetpack Compose's `CompositionLocal` and is organized into the following modular layers:

```
core/ui/src/main/java/org/signal/core/ui/compose/theme/
├── SignalTheme.kt              # Main theme composable (entry point)
├── SignalColorTokens.kt        # Comprehensive color token system
├── SignalTypography.kt         # Typography token system
├── SignalSpacing.kt            # Spacing, sizing, and layout tokens
├── SignalShapes.kt             # Shape and corner radius tokens
├── SignalShadows.kt            # Shadow and elevation tokens
├── SignalMotion.kt             # Motion and animation tokens (existing)
├── SignalInteractiveStates.kt  # Interactive state tokens (hover, focus, pressed, etc.)
├── SignalComponentTokens.kt    # Component-specific tokens for messaging
├── SignalThemeManager.kt       # Centralized theme management
├── ExtendedColors.kt           # Legacy extended color tokens (backward compatible)
├── Dimensions.kt               # Legacy dimension tokens (backward compatible)
├── SnackbarColors.kt           # Snackbar color tokens (existing)
└── Motion.kt                   # Motion/animation tokens (existing)
```

## Quick Start

### Accessing Theme Tokens

```kotlin
// In a @Composable function:
SignalTheme(isDarkMode = isDark) {
  // Access comprehensive token system
  val colors = SignalTheme.tokens.colors
  val typography = SignalTheme.tokens.typography
  val spacing = SignalTheme.tokens.spacing
  val shapes = SignalTheme.tokens.shapes
  val motion = SignalTheme.tokens.motion
  val shadows = SignalTheme.tokens.shadows
  val interactive = SignalTheme.tokens.interactive
  val components = SignalTheme.tokens.components
  
  // Or use the object directly
  SignalTheme.tokens.colors.brand.primary
  SignalTheme.tokens.typography.bodyMedium
  SignalTheme.tokens.spacing.space4
}
```

### Backward Compatibility

Existing code using `SignalTheme.colors` continues to work:

```kotlin
// Legacy usage (still works)
SignalTheme.colors.colorSurface1
SignalTheme.colors.colorTransparent5
```

### Direct CompositionLocal Access

```kotlin
// Access via CompositionLocal
val colorScheme = LocalSignalColorScheme.current
val typography = LocalSignalTypography.current
val spacing = LocalSignalSpacing.current
val shapes = LocalSignalShapes.current
```

## Token System Details

### 1. Color Tokens (`SignalColorTokens.kt`)

Comprehensive color system organized into semantic categories:

- **BrandColors**: Primary, secondary, accent colors and their variants
- **SurfaceColors**: Background layers (surface1-5 for elevation stacking)
- **TextColors**: All text colors (primary, secondary, hint, inverse, etc.)
- **IconColors**: Icon tinting colors for various contexts
- **InteractiveStateColors**: Button, form control, and selection states
- **MessageColors**: Conversation-specific colors (bubbles, status, indicators)
- **StatusColors**: Online/offline, read/unread, delivery states
- **BorderColors**: Dividers, outlines, and borders
- **ComponentColors**: Component-specific colors (toolbar, FAB, search, etc.)
- **TransparencyColors**: Pre-computed transparency levels for overlays
- **ConversationThemeColors**: Wallpaper-aware conversation colors

### 2. Typography Tokens (`SignalTypography.kt`)

Complete typography system with semantic styles:

- Display, Headline, Title, Body, Label (Material 3 standard)
- Caption and CaptionSmall for metadata
- Message-specific: MessageBody, MessageFooter, MessageTimestamp
- Composer: ComposerInput, ComposerHint, ComposerLabel
- Navigation, Button, Toolbar, Settings, Search, Dialog, Notification styles
- Badge, Tab, BottomSheet, Media, Story, Release Notes styles
- Chat-specific, Contacts, Profile, Backup styles
- Error/Warning, Status/Delivery, Emoji, Code styles

### 3. Spacing Tokens (`SignalSpacing.kt`)

All spacing, sizing, and layout tokens on a 4dp base grid:

- Base grid: space0 through space48
- Gutter: gutter, gutterWide, gutterCompact
- Padding: component-level (tiny, small, medium, large, extraLarge)
- Message bubble: bubblePaddingHorizontal, bubblePaddingVertical
- Composer, Toolbar, List items, Settings, Dialog, BottomSheet, Card
- Icon, Avatar, Button, FAB, Input sizing
- Navigation, Toolbar, Tab, Divider, Search, Badge
- Progress, Snackbar, Tooltip, Chip, Reaction pill
- Thumbnail, Audio, Message-specific
- Status indicator, Typing indicator, Wallpaper, Media preview
- QR code, Calendar, Profile

### 4. Shape Tokens (`SignalShapes.kt`)

All corner radius and shape tokens:

- Base: radiusNone, radiusTiny, radiusSmall, radiusMedium, radiusLarge, radiusXLarge, radiusFull
- Component-specific: card, dialog, bottomSheet, button, FAB, input
- Message bubbles: outgoing, incoming, single, first, last
- Quote, Reaction, Avatar, List item, Navigation, Tab
- Badge, Progress, Snackbar, Tooltip, Image, Media
- QR code, Calendar, Story, Profile, Settings, Search
- Contact, Attachment, Menu, Toggle, Donation, Selectable

### 5. Shadow Tokens (`SignalShadows.kt`)

Elevation and shadow system:

- Elevation levels: elevationNone, elevationFlat, elevationLow, elevationMedium, elevationHigh, elevationHighest
- Component elevation: card, dialog, bottomSheet, FAB, navigationBar
- Bubble elevation: messageBubble, messageBubbleSelected
- Avatar, Progress, Overlay, Scrim

### 6. Interactive State Tokens (`SignalInteractiveStates.kt`)

Complete interactive state system:

- **ButtonState**: background, foreground, border, ripple, elevation
- **ToggleState**: track, thumb, checkmark, border
- **InputState**: background, text, border, placeholder, cursor, highlight
- **ListItemState**: background, foreground, icon, selectedBackground, ripple
- **TabState**: background, text, icon, indicator, ripple
- **ChipState**: background, foreground, border, icon, ripple
- **LinkState**: text, underline, visited
- **BottomSheetState**: background, handle, scrim
- **MessageState**: background, foreground, overlay, selection
- **SearchState**: background, text, placeholder, icon
- **AvatarState**: background, foreground, border, ring
- **StoryState**: ring, background, foreground
- **NotificationState**: background, foreground, badge, badgeText

States include: default, pressed, disabled, hovered, focused, loading, selected, active, error, success

### 7. Component Tokens (`SignalComponentTokens.kt`)

Component-specific tokens for every messaging interface:

- **ConversationComponentTokens**: Toolbar, header, date separator, scroll-to-bottom
- **ChatListComponentTokens**: Item, avatar, name, preview, timestamp, badge, separator
- **ComposerComponentTokens**: Container, input, divider, buttons, voice note, media keyboard
- **AttachmentComponentTokens**: Container, thumbnail, file, image, document, link preview, quote
- **MediaPreviewComponentTokens**: Bar, thumbnail, selection, overlay
- **ContactComponentTokens**: Name, subtitle, detail, avatar, checkbox, filter
- **ProfileComponentTokens**: Name, bio, status, avatar, section, action
- **NotificationComponentTokens**: Background, badge, title, body, timestamp, icon
- **SearchComponentTokens**: Background, text, result, tab, highlight
- **DialogComponentTokens**: Background, title, body, action, divider, scrim
- **MenuComponentTokens**: Background, icon, divider, item, elevation
- **SettingsComponentTokens**: Background, surface, text, section, icon, ripple, divider
- **CallComponentTokens**: Background, foreground, icon, hangup, answer, controls
- **StoryComponentTokens**: Background, foreground, ring, bullet, gradient
- **BackupComponentTokens**: Background, foreground, title, body, progress
- **RegistrationComponentTokens**: Background, input, button, top background

## Design Principles

### 1. Centralized and Standardized
All visual and UI settings are defined in one place and accessed through a single API. No duplicate configuration.

### 2. Modular and Organized
Tokens are organized by category (colors, typography, spacing, etc.) and subcategory (surface, text, interactive, etc.). Easy to find and modify.

### 3. Consistent and Scalable
All tokens follow a consistent naming convention and are based on a 4dp grid. Adding new tokens is straightforward.

### 4. Easy to Maintain
Changing a theme globally requires modifying only the token definitions. No need to hunt through dozens of files.

### 5. Backward Compatible
Existing code using `SignalTheme.colors`, `Dimensions`, and `Motion` continues to work unchanged.

### 6. Extensible
New token categories can be added by creating new data classes and adding them to the `SignalThemeTokens` object.

## Usage Examples

### Accessing Colors
```kotlin
val colors = SignalTheme.tokens.colors
// Brand
val primary = colors.brand.primary
val primaryContainer = colors.brand.primaryContainer
// Surface
val background = colors.surface.background
val surface1 = colors.surface.surface1
// Text
val textPrimary = colors.text.primary
val textSecondary = colors.text.secondary
// Message
val outgoingBubble = colors.message.outgoingBubble
val incomingBubble = colors.message.incomingBubble
// Status
val online = colors.status.online
val unreadBadge = colors.status.unreadBadge
// Interactive
val buttonDefault = colors.interactive.buttonPrimary
val buttonDisabled = colors.interactive.buttonPrimaryDisabled
```

### Accessing Typography
```kotlin
val typography = SignalTheme.tokens.typography
val bodyMedium = typography.bodyMedium
val titleLarge = typography.titleLarge
val messageBody = typography.messageBody
val composerInput = typography.composerInput
```

### Accessing Spacing
```kotlin
val spacing = SignalTheme.tokens.spacing
val padding = spacing.paddingMedium  // 16.dp
val gap = spacing.gapSmall  // 8.dp
val iconSize = spacing.iconSizeMedium  // 20.dp
val avatarSize = spacing.avatarSizeMedium  // 40.dp
```

### Accessing Shapes
```kotlin
val shapes = SignalTheme.tokens.shapes
val cardShape = shapes.card  // RoundedCornerShape(12.dp)
val dialogShape = shapes.dialog  // RoundedCornerShape(28.dp)
val messageBubble = shapes.messageBubbleOutgoing
```

### Accessing Interactive States
```kotlin
val interactive = SignalTheme.tokens.interactive
val buttonState = interactive.buttonDefault
val inputState = interactive.inputFocused
val listItemState = interactive.listItemDefault
```

### Accessing Component Tokens
```kotlin
val components = SignalTheme.tokens.components
val toolbarBg = components.conversation.toolbarBackground
val composerBg = components.composer.background
val chatListItemHeight = components.chatList.itemHeight
```

## Integration with XML Resources

The Compose theme system works alongside the existing XML resource system. The `light_colors.xml` and `dark_colors.xml` XML files continue to define color resources for XML-based views. The Compose theme system provides additional tokens for Compose-based UI.

## Adding New Tokens

To add a new token:

1. Add the property to the appropriate data class (e.g., `BrandColors`, `MessageColors`, etc.)
2. Update the `createLightColorScheme()` and `createDarkColorScheme()` functions
3. Update the default value in the `CompositionLocal` (for data classes)
4. Add the property to the `SignalThemeTokens` object if it's a new category

## File Inventory

| File | Lines | Purpose |
|------|-------|---------|
| `SignalTheme.kt` | ~400 | Main theme composable and entry point |
| `SignalColorTokens.kt` | ~1600 | Comprehensive color token system |
| `SignalTypography.kt` | ~370 | Typography token system |
| `SignalSpacing.kt` | ~510 | Spacing, sizing, and layout tokens |
| `SignalShapes.kt` | ~360 | Shape and corner radius tokens |
| `SignalShadows.kt` | ~115 | Shadow and elevation tokens |
| `SignalInteractiveStates.kt` | ~660 | Interactive state tokens |
| `SignalComponentTokens.kt` | ~810 | Component-specific tokens |
| `SignalThemeManager.kt` | ~110 | Centralized theme management |
| `ExtendedColors.kt` | ~63 | Legacy extended colors (backward compatible) |
| `Dimensions.kt` | ~64 | Legacy dimensions (backward compatible) |
| `Motion.kt` | ~44 | Motion/animation tokens (existing) |
| `SnackbarColors.kt` | ~36 | Snackbar colors (existing) |
| **Total** | **~5134** | **Complete theme system** |

## License

Copyright 2025 Signal Messenger, LLC
SPDX-License-Identifier: AGPL-3.0-only