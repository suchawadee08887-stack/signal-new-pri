package org.signal.core.ui.compose.theme

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import org.signal.core.ui.CoreUiDependencies
import org.signal.core.ui.compose.ProvideIncognitoKeyboard

// ============================================================================
// Typography - Material 3 compatible with Signal overrides
// ============================================================================

private val typography = androidx.compose.material3.Typography().run {
  copy(
    headlineLarge = headlineLarge.copy(
      fontSize = 32.sp,
      lineHeight = 40.sp,
      letterSpacing = 0.sp
    ),
    headlineMedium = headlineMedium.copy(
      fontSize = 28.sp,
      lineHeight = 36.sp,
      letterSpacing = 0.sp
    ),
    titleLarge = titleLarge.copy(
      fontSize = 22.sp,
      lineHeight = 28.sp,
      letterSpacing = 0.sp
    ),
    titleMedium = titleMedium.copy(
      fontSize = 18.sp,
      lineHeight = 24.sp,
      letterSpacing = 0.0125.sp,
      fontFamily = FontFamily.SansSerif,
      fontStyle = FontStyle.Normal
    ),
    titleSmall = titleSmall.copy(
      fontSize = 16.sp,
      lineHeight = 22.sp,
      letterSpacing = 0.0125.sp
    ),
    bodyLarge = bodyLarge.copy(
      fontSize = 16.sp,
      lineHeight = 22.sp,
      letterSpacing = 0.0125.sp
    ),
    bodyMedium = bodyMedium.copy(
      fontSize = 14.sp,
      lineHeight = 20.sp,
      letterSpacing = 0.0107.sp
    ),
    bodySmall = bodySmall.copy(
      fontSize = 13.sp,
      lineHeight = 16.sp,
      letterSpacing = 0.0192.sp
    ),
    labelLarge = labelLarge.copy(
      fontSize = 14.sp,
      lineHeight = 20.sp,
      letterSpacing = 0.0107.sp
    ),
    labelMedium = labelMedium.copy(
      fontSize = 13.sp,
      lineHeight = 16.sp,
      letterSpacing = 0.0192.sp
    ),
    labelSmall = labelSmall.copy(
      fontSize = 12.sp,
      lineHeight = 16.sp,
      letterSpacing = 0.025.sp
    )
  )
}

// ============================================================================
// Material 3 Color Schemes (existing)
// ============================================================================

private val lightColorScheme = lightColorScheme(
  primary = Color(0xFF2C58C3),
  primaryContainer = Color(0xFFD2DFFB),
  secondary = Color(0xFF586071),
  secondaryContainer = Color(0xFFDCE5F9),
  surface = Color(0xFFFBFCFF),
  surfaceContainerLow = Color(0xFFF2F5F9),
  surfaceContainerHighest = Color(0xFFE7EBF3),
  surfaceVariant = Color(0xFFE7EBF3),
  background = Color(0xFFFBFCFF),
  error = Color(0xFFBA1B1B),
  errorContainer = Color(0xFFFFDAD4),
  onPrimary = Color(0xFFFFFFFF),
  onPrimaryContainer = Color(0xFF051845),
  onSecondary = Color(0xFFFFFFFF),
  onSecondaryContainer = Color(0xFF151D2C),
  onSurface = Color(0xFF1B1B1D),
  onSurfaceVariant = Color(0xFF545863),
  onBackground = Color(0xFF1B1D1D),
  outline = Color(0xFF808389)
)

private val lightExtendedColors = ExtendedColors(
  neutralSurface = Color(0x99FFFFFF),
  colorOnCustom = Color(0xFFFFFFFF),
  colorOnCustomVariant = Color(0xB3FFFFFF),
  colorSurface1 = Color(0xFFF2F5F9),
  colorSurface2 = Color(0xFFEDF0F6),
  colorSurface3 = Color(0xFFE8ECF4),
  colorSurface4 = Color(0xFFE6EAF3),
  colorSurface5 = Color(0xFFE3E7F1),
  colorTransparent1 = Color(0x14FFFFFF),
  colorTransparent2 = Color(0x29FFFFFF),
  colorTransparent3 = Color(0x8FFFFFFF),
  colorTransparent4 = Color(0xB8FFFFFF),
  colorTransparent5 = Color(0xF5FFFFFF),
  colorNeutral = Color(0xFFFFFFFF),
  colorNeutralVariant = Color(0xB8FFFFFF),
  colorTransparentInverse1 = Color(0x0A000000),
  colorTransparentInverse2 = Color(0x14000000),
  colorTransparentInverse3 = Color(0x66000000),
  colorTransparentInverse4 = Color(0xB8000000),
  colorTransparentInverse5 = Color(0xE0000000),
  colorNeutralInverse = Color(0xFF121212),
  colorNeutralVariantInverse = Color(0xFF5C5C5C),
  colorWarning = Color(0x1FB44828),
  colorOnWarning = Color(0xFFB44828)
)

private val darkExtendedColors = ExtendedColors(
  neutralSurface = Color(0x14FFFFFF),
  colorOnCustom = Color(0xFFFFFFFF),
  colorOnCustomVariant = Color(0x18FFFFFF),
  colorSurface1 = Color(0xFF23242A),
  colorSurface2 = Color(0xFF272A31),
  colorSurface3 = Color(0xFF2C2F37),
  colorSurface4 = Color(0xFF2E3039),
  colorSurface5 = Color(0xFF31343E),
  colorTransparent1 = Color(0x0AFFFFFF),
  colorTransparent2 = Color(0x1FFFFFFF),
  colorTransparent3 = Color(0x29FFFFFF),
  colorTransparent4 = Color(0x7AFFFFFF),
  colorTransparent5 = Color(0xB8FFFFFF),
  colorNeutral = Color(0xFF121212),
  colorNeutralVariant = Color(0xFF5C5C5C),
  colorTransparentInverse1 = Color(0x0A000000),
  colorTransparentInverse2 = Color(0x14000000),
  colorTransparentInverse3 = Color(0x29000000),
  colorTransparentInverse4 = Color(0xB8000000),
  colorTransparentInverse5 = Color(0xF5000000),
  colorNeutralInverse = Color(0xE0FFFFFF),
  colorNeutralVariantInverse = Color(0xA3FFFFFF),
  colorWarning = Color(0x1FEB977D),
  colorOnWarning = Color(0xFFEB977D)
)

private val darkColorScheme = darkColorScheme(
  primary = Color(0xFFB6C5FA),
  primaryContainer = Color(0xFF464B5C),
  secondary = Color(0xFFC1C6DD),
  secondaryContainer = Color(0xFF414659),
  surface = Color(0xFF1B1C1F),
  surfaceContainerLow = Color(0xFF23242A),
  surfaceContainerHighest = Color(0xFF303133),
  surfaceVariant = Color(0xFF303133),
  background = Color(0xFF1B1C1F),
  error = Color(0xFFFFB4A9),
  errorContainer = Color(0xFF930006),
  onPrimary = Color(0xFF1E2438),
  onPrimaryContainer = Color(0xFFDBE1FC),
  onSecondary = Color(0xFF2A3042),
  onSecondaryContainer = Color(0xFFDCE1F9),
  onSurface = Color(0xFFE2E1E5),
  onSurfaceVariant = Color(0xFFBEBFC5),
  onBackground = Color(0xFFE2E1E5),
  outline = Color(0xFF5C5E65)
)

private val lightSnackbarColors = SnackbarColors(
  color = darkColorScheme.surface,
  contentColor = darkColorScheme.onSurface,
  actionColor = darkColorScheme.primary,
  actionContentColor = darkColorScheme.primary,
  dismissActionContentColor = darkColorScheme.onSurface
)

private val darkSnackbarColors = SnackbarColors(
  color = darkColorScheme.surfaceVariant,
  contentColor = darkColorScheme.onSurfaceVariant,
  actionColor = darkColorScheme.primary,
  actionContentColor = darkColorScheme.primary,
  dismissActionContentColor = darkColorScheme.onSurfaceVariant
)

// ============================================================================
// Main Theme Composable - Integrates all token systems
// ============================================================================

/**
 * The main Signal theme composable that provides all design tokens.
 *
 * This composable provides:
 * - Material 3 color scheme (light/dark)
 * - Extended color tokens (custom surfaces, transparency, etc.)
 * - Signal color scheme (comprehensive semantic color tokens)
 * - Typography tokens
 * - Spacing tokens
 * - Shape tokens
 * - Motion tokens
 * - Shadow tokens
 * - Interactive state tokens
 * - Snackbar colors
 *
 * All tokens are provided via CompositionLocal and can be accessed through:
 * - [SignalTheme.colors] - Extended colors (backward compatible)
 * - [SignalTheme.tokens] - All new token systems
 *
 * Example usage:
 * ```kotlin
 * SignalTheme(isDarkMode = isDark) {
 *   // Access colors
 *   SignalTheme.colors.colorSurface1
 *
 *   // Access comprehensive token system
 *   SignalTheme.tokens.colors.brand.primary
 *   SignalTheme.tokens.typography.bodyMedium
 *   SignalTheme.tokens.spacing.space4
 *   SignalTheme.tokens.shapes.radiusMedium
 *   SignalTheme.tokens.motion.durationMedium1
 *   SignalTheme.tokens.shadows.elevationMedium
 *   SignalTheme.tokens.interactive.buttonDefault
 * }
 * ```
 */
@Composable
fun SignalTheme(
  isDarkMode: Boolean = LocalConfiguration.current.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES,
  incognitoKeyboardEnabled: Boolean = CoreUiDependencies.isIncognitoKeyboardEnabled,
  content: @Composable () -> Unit
) {
  val extendedColors = if (isDarkMode) darkExtendedColors else lightExtendedColors
  val snackbarColors = if (isDarkMode) darkSnackbarColors else lightSnackbarColors
  val colorScheme = if (isDarkMode) darkColorScheme else lightColorScheme
  val signalColorScheme = if (isDarkMode) createDarkColorScheme() else createLightColorScheme()
  val signalTypography = signalTypography()
  val signalSpacing = signalSpacing()
  val signalShapes = signalShapes()
  val signalShadows = signalShadows()
  val signalInteractive = signalInteractiveStates(signalColorScheme)
  val signalComponentTokens = createDefaultComponentTokens(signalColorScheme)

  ProvideIncognitoKeyboard(enabled = incognitoKeyboardEnabled) {
    CompositionLocalProvider(
      // Existing
      LocalExtendedColors provides extendedColors,
      LocalSnackbarColors provides snackbarColors,
      // New token systems
      LocalSignalColorScheme provides signalColorScheme,
      LocalSignalTypography provides signalTypography,
      LocalSignalSpacing provides signalSpacing,
      LocalSignalShapes provides signalShapes,
      LocalSignalShadows provides signalShadows,
      LocalSignalInteractiveStates provides signalInteractive,
      LocalSignalComponentTokens provides signalComponentTokens
    ) {
      MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
      )
    }
  }
}

/**
 * Applies the light color scheme to [content] regardless of the ambient theme,
 * leaving typography and shapes untouched.
 */
@Composable
fun ForceLightColors(content: @Composable () -> Unit) {
  CompositionLocalProvider(
    LocalExtendedColors provides lightExtendedColors,
    LocalSignalColorScheme provides createLightColorScheme()
  ) {
    MaterialTheme(
      colorScheme = lightColorScheme,
      content = content
    )
  }
}

// ============================================================================
// Backward-Compatible Theme Object
// ============================================================================

/**
 * Main theme accessor object.
 *
 * Provides backward-compatible access to extended colors via [SignalTheme.colors],
 * and also provides access to the comprehensive token system via [SignalTheme.tokens].
 *
 * Legacy usage (backward compatible):
 * ```kotlin
 * SignalTheme.colors.colorSurface1
 * ```
 *
 * New usage (comprehensive):
 * ```kotlin
 * SignalTheme.tokens.colors.brand.primary
 * SignalTheme.tokens.typography.bodyMedium
 * SignalTheme.tokens.spacing.space4
 * ```
 */
object SignalTheme {
  /** Backward-compatible access to extended colors. */
  val colors: ExtendedColors
    @Composable
    get() = LocalExtendedColors.current

  /** Access to all comprehensive theme tokens. */
  val tokens: SignalThemeTokens
    @Composable
    get() = SignalThemeTokens
}

// ============================================================================
// Preview
// ============================================================================

@Preview(showBackground = true)
@Composable
private fun TypographyPreview() {
  SignalTheme(
    isDarkMode = false,
    incognitoKeyboardEnabled = false
  ) {
    Column {
      Text(
        text = "Headline Large",
        style = MaterialTheme.typography.headlineLarge
      )
      Text(
        text = "Headline Medium",
        style = MaterialTheme.typography.headlineMedium
      )
      Text(
        text = "Headline Small",
        style = MaterialTheme.typography.headlineSmall
      )
      Text(
        text = "Title Large",
        style = MaterialTheme.typography.titleLarge
      )
      Text(
        text = "Title Medium",
        style = MaterialTheme.typography.titleMedium
      )
      Text(
        text = "Title Small",
        style = MaterialTheme.typography.titleSmall
      )
      Text(
        text = "Body Large",
        style = MaterialTheme.typography.bodyLarge
      )
      Text(
        text = "Body Medium",
        style = MaterialTheme.typography.bodyMedium
      )
      Text(
        text = "Body Small",
        style = MaterialTheme.typography.bodySmall
      )
      Text(
        text = "Label Large",
        style = MaterialTheme.typography.labelLarge
      )
      Text(
        text = "Label Medium",
        style = MaterialTheme.typography.labelMedium
      )
      Text(
        text = "Label Small",
        style = MaterialTheme.typography.labelSmall
      )
    }
  }
}