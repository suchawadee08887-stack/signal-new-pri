# Signal Android Comprehensive Theme Management System

## Overview

A centralized, modular, and scalable theme management system for the Signal Android messaging application. The system standardizes all visual and UI settings across every screen and component, enabling global theme changes without duplicating configuration.

## Architecture

The theme system is built on Jetpack Compose's `CompositionLocal` and is organized into the following modular layers in `core/ui/src/main/java/org/signal/core/ui/compose/theme/`:

| File | Lines | Purpose |
|------|-------|---------|
| `SignalTheme.kt` | 398 | Main theme composable (entry point) |
| `SignalColorTokens.kt` | 1603 | Comprehensive color token system |
| `SignalTypography.kt` | 371 | Typography token system |
| `SignalSpacing.kt` | 508 | Spacing, sizing, and layout tokens |
| `SignalShapes.kt` | 359 | Shape and corner radius tokens |
| `SignalShadows.kt` | 114 | Shadow and elevation tokens |
| `SignalInteractiveStates.kt` | 661 | Interactive state tokens |
| `SignalComponentTokens.kt` | 808 | Component-specific tokens |
| `SignalThemeManager.kt` | 72 | Centralized theme management |
| `ExtendedColors.kt` | 62 | Legacy (backward compatible) |
| `Dimensions.kt` | 64 | Legacy (backward compatible) |
| `Motion.kt` | 44 | Motion/animation tokens |
| `SnackbarColors.kt` | 35 | Snackbar colors |
| **Total** | **5099** | **Complete theme system** |

## Quick Start

### Accessing All Tokens
```kotlin
SignalTheme(isDarkMode = isDark) {
  val colors = SignalTheme.tokens.colors
  val typography = SignalTheme.tokens.typography
  val spacing = SignalTheme.tokens.spacing
  val shapes = SignalTheme.tokens.shapes
  val shadows = SignalTheme.tokens.shadows
  val interactive = SignalTheme.tokens.interactive
  val components = SignalTheme.tokens.components
}
```

### Backward Compatible (Existing Code)
```kotlin
SignalTheme.colors.colorSurface1
SignalTheme.colors.colorTransparent5
```

### Direct CompositionLocal Access
```kotlin
val colorScheme = LocalSignalColorScheme.current
val typography = LocalSignalTypography.current
val spacing = LocalSignalSpacing.current
```

## Token Categories

### Colors (SignalColorTokens.kt)
- **BrandColors**: Primary, secondary, accent colors and variants
- **SurfaceColors**: Background layers (surface1-5 for elevation stacking)
- **TextColors**: Primary, secondary, tertiary, hint, inverse, link, destructive
- **IconColors**: Primary, secondary, action, tab selected/unselected
- **InteractiveStateColors**: Button, form, selection, hover/focus/pressed/disabled states
- **MessageColors**: Bubbles, text, delivery status, read/unread, typing, reactions, media, view-once, mention, pulse, message requests, safety tips, gifts
- **StatusColors**: Online/offline, delivery, verification, error/warning/success/info, unread badge
- **BorderColors**: Dividers, borders, outline
- **ComponentColors**: Toolbar, FAB, search, chat list, navigation, bottom sheet, snackbar, toast, tooltip, composer, media, contacts, wallpaper, story, payment, insights, QR, release notes, voice note, etc.
- **TransparencyColors**: Standard and inverse overlays (5%-80%)
- **ConversationThemeColors**: Wallpaper-aware conversation colors

### Typography (SignalTypography.kt)
- Display, Headline, Title, Body, Label (Material 3 standard)
- Caption and CaptionSmall for metadata
- Message-specific: MessageBody, MessageFooter, MessageTimestamp
- Composer, Navigation, Button, Toolbar, Settings, Search, Dialog
- Notification, Badge, Tab, BottomSheet, Media, Story, Release Notes
- Chat-specific, Contacts, Profile, Backup
- Error/Warning, Status/Delivery, Emoji, Code

### Spacing (SignalSpacing.kt)
- Base grid: space0 through space48 (4dp base)
- Gutter, padding, margin, gap
- Bubble, composer, toolbar, list items, settings, dialog, bottom sheet, card
- Icon, avatar, button, FAB, input sizing
- Navigation, toolbar, tab, divider, search, badge
- Progress, snackbar, tooltip, chip, reaction pill
- Thumbnail, audio, message, status indicator, typing indicator
- Wallpaper, media preview, QR code, calendar, profile

### Shapes (SignalShapes.kt)
- Base: radiusNone, radiusTiny, radiusSmall, radiusMedium, radiusLarge, radiusXLarge, radiusFull
- Component: card, dialog, bottom sheet, button, FAB, input
- Message bubbles: outgoing, incoming, single, first, last
- Quote, reaction, avatar, list item, navigation, tab
- Badge, progress, snackbar, tooltip, image, media
- QR code, calendar, story, profile, settings, search
- Contact, attachment, menu, toggle, donation, selectable

### Shadows (SignalShadows.kt)
- Elevation levels: none, flat, low, medium, high, highest
- Component elevation: card, dialog, bottom sheet, FAB, navigation
- Bubble elevation, avatar, progress, overlay, scrim

### Interactive States (SignalInteractiveStates.kt)
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

### Component Tokens (SignalComponentTokens.kt)
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

1. **Centralized**: All visual settings in one place, accessed through a single API
2. **Modular**: Tokens organized by category and subcategory
3. **Consistent**: All tokens follow naming conventions and 4dp grid
4. **Scalable**: New tokens can be added without modifying existing code
5. **Backward Compatible**: Existing code using `SignalTheme.colors`, `Dimensions`, `Motion` works unchanged
6. **Extensible**: New token categories can be added by creating new data classes

## Existing Code Compatibility

The existing `ExtendedColors.kt`, `Dimensions.kt`, `Motion.kt`, and `SnackbarColors.kt` files are preserved for backward compatibility. The new token system extends and complements them. Existing code using `SignalTheme.colors` continues to work without modification.

## License

Copyright 2025 Signal Messenger, LLC
SPDX-License-Identifier: AGPL-3.0-only