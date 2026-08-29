package com.pairlink.app.core.designsystem

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = DesignTokens.Colors.PrimaryCrimson,
    onPrimary = Color.White,
    primaryContainer = DesignTokens.Colors.LightPinkHighlight,
    onPrimaryContainer = DesignTokens.Colors.PrimaryCrimson,
    secondary = DesignTokens.Colors.SoftPink,
    onSecondary = DesignTokens.Colors.TextPrimary,
    secondaryContainer = DesignTokens.Colors.Lavender,
    onSecondaryContainer = DesignTokens.Colors.TextPrimary,
    tertiary = DesignTokens.Colors.VibrantPink,
    onTertiary = Color.White,
    background = DesignTokens.Colors.BackgroundMidnight,
    onBackground = DesignTokens.Colors.TextPrimary,
    surface = Color.White,
    onSurface = DesignTokens.Colors.TextPrimary,
    surfaceVariant = Color(0xFFFFF0F3),
    onSurfaceVariant = DesignTokens.Colors.TextSecondary,
    outline = Color(0xFFF4C2CC),
    error = DesignTokens.Colors.DangerRose,
    onError = Color.White
)

@Composable
fun PairLinkTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = DesignTokens.Colors.BackgroundMidnight.toArgb()
            window.navigationBarColor = DesignTokens.Colors.BackgroundMidnight.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = true
        }
    }

    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = PairLinkTypography,
        content = content
    )
}
