package com.pairlink.app.core.designsystem

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = DesignTokens.Colors.PrimaryPink,
    onPrimary = DesignTokens.Colors.DeepPinkText,
    primaryContainer = DesignTokens.Colors.SoftPink.copy(alpha = 0.2f),
    onPrimaryContainer = DesignTokens.Colors.PrimaryPink,
    secondary = DesignTokens.Colors.Lavender,
    onSecondary = DesignTokens.Colors.DeepPurple,
    secondaryContainer = DesignTokens.Colors.MutedPurple.copy(alpha = 0.3f),
    onSecondaryContainer = DesignTokens.Colors.LightLavender,
    tertiary = DesignTokens.Colors.VibrantPink,
    onTertiary = Color.White,
    background = DesignTokens.Colors.BackgroundMidnight,
    onBackground = DesignTokens.Colors.TextPrimary,
    surface = DesignTokens.Colors.BackgroundDarkSurface.copy(alpha = 0.6f),
    onSurface = DesignTokens.Colors.TextPrimary,
    surfaceVariant = Color(0x1AFFFFFF),
    onSurfaceVariant = DesignTokens.Colors.TextSecondary,
    outline = Color(0x26FFFFFF),
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
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = PairLinkTypography,
        content = content
    )
}
