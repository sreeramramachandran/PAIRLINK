package com.pairlink.app.core.designsystem

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Global Design Tokens for PairLink.
 * Light Neumorphic & Crimson Glass Theme.
 */
object DesignTokens {

    object Colors {
        // Light Warm Blush Palette
        val BackgroundMidnight = Color(0xFFFAF5F5)
        val BackgroundDarkSurface = Color(0xFFFFFFFF)
        val DeepPurple = Color(0xFFFFF0F3)
        val DarkNavy = Color(0xFFFAF5F5)
        val MutedPurple = Color(0xFFFFE6EC)

        // Crimson & Rose Highlights (Reference Image Palette)
        val PrimaryCrimson = Color(0xFFE60039)
        val CrimsonDark = Color(0xFFC4002F)
        val SoftPink = Color(0xFFFF809B)
        val PrimaryPink = Color(0xFFE60039)
        val LightPinkHighlight = Color(0xFFFFF0F3)
        val VibrantPink = Color(0xFFFF2E63)
        val DeepPinkText = Color(0xFFFFFFFF)
        val DarkRoseAccent = Color(0xFFE60039)

        // Lavender & Soft Accents
        val Lavender = Color(0xFFFFEBF0)
        val LightLavender = Color(0xFFFFF5F7)

        // High-Contrast Text & Contrast
        val TextPrimary = Color(0xFF140A0D)
        val TextSecondary = Color(0xFF4A3E43)
        val TextMuted = Color(0xFF6A5D63)
        val TextWhite = Color(0xFFFFFFFF)

        // Status & Alerts
        val OnlineGreen = Color(0xFF10B981)
        val DangerRose = Color(0xFFE60039)
        val DangerRoseSurface = Color(0x1AE60039)
        val SkyBlue = Color(0xFF38BDF8)
    }

    object Glass {
        // Translucent Alpha Levels for Light Glass Panels
        const val BackgroundAlphaSubtle = 0.60f
        const val BackgroundAlphaDefault = 0.88f
        const val BackgroundAlphaProminent = 0.95f
        const val BackgroundAlphaCard = 0.90f

        const val BorderAlphaSubtle = 0.40f
        const val BorderAlphaHighlight = 0.80f
        const val BorderAlphaFocus = 1.00f

        val BorderWidthThin: Dp = 1.dp
        val BorderWidthThick: Dp = 2.dp

        // Glow Color Definitions
        val GlowPrimary = Color(0xFFE60039).copy(alpha = 0.35f)
        val GlowIntense = Color(0xFFE60039).copy(alpha = 0.55f)
        val GlowGreen = Color(0xFF10B981).copy(alpha = 0.50f)
        val GlowRose = Color(0xFFE60039).copy(alpha = 0.40f)
        val GlowLavender = Color(0xFFFF809B).copy(alpha = 0.40f)
    }

    object Radius {
        val Small: Dp = 12.dp
        val Medium: Dp = 16.dp
        val Large: Dp = 24.dp
        val ExtraLarge: Dp = 28.dp
        val SuperLarge: Dp = 32.dp
        val Hero: Dp = 40.dp
        val Full: Dp = 9999.dp
    }

    object Spacing {
        val XSmall: Dp = 4.dp
        val Small: Dp = 8.dp
        val Medium: Dp = 12.dp
        val Large: Dp = 16.dp
        val XLarge: Dp = 20.dp
        val XXLarge: Dp = 24.dp
        val Section: Dp = 32.dp
        val ScreenPadding: Dp = 20.dp
        val BottomBarHeight: Dp = 72.dp
        val TopBarHeight: Dp = 64.dp
    }

    object IconSize {
        val Small: Dp = 18.dp
        val Medium: Dp = 24.dp
        val Large: Dp = 32.dp
        val XLarge: Dp = 42.dp
        val Hero: Dp = 64.dp
    }

    object Elevation {
        val Card: Dp = 6.dp
        val Floating: Dp = 12.dp
        val Modal: Dp = 20.dp
    }

    object Animation {
        const val DurationFast = 200
        const val DurationNormal = 350
        const val DurationSlow = 600
        const val DurationPulse = 2400
        const val DurationMeshMovement = 8000
    }
}
