package com.pietervandewalle.androidapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

private var successColorSchemeColor by mutableStateOf(light_success)
private var successContainerColorSchemeColor by mutableStateOf(light_successContainer)

private var warningColorSchemeColor by mutableStateOf(light_warning)
private var warningContainerColorSchemeColor by mutableStateOf(light_warningContainer)

@Suppress("unused")
var ColorScheme.success: Color
    get() = successColorSchemeColor
    set(value) {
        successColorSchemeColor = value
    }

@Suppress("unused")
var ColorScheme.successContainer: Color
    get() = successContainerColorSchemeColor
    set(value) {
        successContainerColorSchemeColor = value
    }

@Suppress("unused")
var ColorScheme.warning: Color
    get() = warningColorSchemeColor
    set(value) {
        warningColorSchemeColor = value
    }

@Suppress("unused")
var ColorScheme.warningContainer: Color
    get() = warningContainerColorSchemeColor
    set(value) {
        warningContainerColorSchemeColor = value
    }

private val LightColors = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    errorContainer = md_theme_light_errorContainer,
    onError = md_theme_light_onError,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    outline = md_theme_light_outline,
    inverseOnSurface = md_theme_light_inverseOnSurface,
    inverseSurface = md_theme_light_inverseSurface,
    inversePrimary = md_theme_light_inversePrimary,
    surfaceTint = md_theme_light_surfaceTint,
    outlineVariant = md_theme_light_outlineVariant,
    scrim = md_theme_light_scrim,
)

private val DarkColors = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    errorContainer = md_theme_dark_errorContainer,
    onError = md_theme_dark_onError,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    outline = md_theme_dark_outline,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inverseSurface = md_theme_dark_inverseSurface,
    inversePrimary = md_theme_dark_inversePrimary,
    surfaceTint = md_theme_dark_surfaceTint,
    outlineVariant = md_theme_dark_outlineVariant,
    scrim = md_theme_dark_scrim,
)

/**
 * Composable function to apply the Android app theme.
 *
 * @param useDarkTheme Whether to use the dark theme or not.
 * @param content The composable content of the app.
 */
@Composable
fun AndroidAppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (!useDarkTheme) {
        LightColors
    } else {
        DarkColors
    }

    // Extra colors
    colorScheme.success = if (!useDarkTheme) {
        success
    } else {
        dark_success
    }

    colorScheme.successContainer = if (!useDarkTheme) {
        light_successContainer
    } else {
        dark_successContainer
    }

    colorScheme.warning = if (!useDarkTheme) {
        warning
    } else {
        dark_warning
    }

    colorScheme.warningContainer = if (!useDarkTheme) {
        light_warningContainer
    } else {
        dark_warningContainer
    }

    MaterialTheme(
        colorScheme = colors,
        content = content,
        typography = Typography,
    )
}
