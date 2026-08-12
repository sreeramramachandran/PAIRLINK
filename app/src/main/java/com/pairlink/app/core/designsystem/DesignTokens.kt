package com.pairlink.app.core.designsystem

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Global Design Tokens for PairLink.
 * Ensures strict visual consistency across all screens and components.
 */
object DesignTokens {

    object Colors {
        // Romantic Midnight Palette
        val BackgroundMidnight = Color(0xFF0B1326)
        val BackgroundDarkSurface = Color(0xFF131A33)
        val DeepPurple = Color(0xFF301261)
        val DarkNavy = Color(0xFF0A142E)
        val MutedPurple = Color(0xFF593D5F)

        // Pink & Rose Highlights
        val SoftPink = Color(0xFFF4A7B9)
        val PrimaryPink = Color(0xFFFFCBD5)
        val LightPinkHighlight = Color(0xFFFFD9E0)
        val VibrantPink = Color(0xFFFFA3AB)
        val DeepPinkText = Color(0xFF521F2E)
        val DarkRoseAccent = Color(0xFF733949)

        // Lavender & Violet Accents
        val Lavender = Color(0xFFDFBBE4)
        val LightLavender = Color(0xFFFCD7FF)

        // Text & Contrast
        val TextPrimary = Color(0xFFDAE2FD)
        val TextSecondary = Color(0xFFD6C1C5)
        val TextMuted = Color(0xFF9E95A2)
        val TextWhite = Color(0xFFFFFFFF)

        // Status & Alerts
        val OnlineGreen = Color(0xFF34D399)
        val DangerRose = Color(0xFFFB7185)
        val DangerRoseSurface = Color(0x33FB7185)
        val SkyBlue = Color(0xFF38BDF8)
    }

    object Glass {
        // Translucent Alpha Levels
        const val BackgroundAlphaSubtle = 0.05f
        const val BackgroundAlphaDefault = 0.10f
        const val BackgroundAlphaProminent = 0.15f
        const val BackgroundAlphaCard = 0.12f

        const val BorderAlphaSubtle = 0.15f
        const val BorderAlphaHighlight = 0.35f
        const val BorderAlphaFocus = 0.60f

        val BorderWidthThin: Dp = 1.dp
        val BorderWidthThick: Dp = 2.dp

        // Glow Color Definitions
        val GlowPrimary = Color(0xFFF4A7B9).copy(alpha = 0.45f)
        val GlowIntense = Color(0xFFF4A7B9).copy(alpha = 0.75f)
        val GlowGreen = Color(0xFF34D399).copy(alpha = 0.60f)
        val GlowRose = Color(0xFFFB7185).copy(alpha = 0.50f)
        val GlowLavender = Color(0xFFDFBBE4).copy(alpha = 0.50f)
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
        val Card: Dp = 8.dp
        val Floating: Dp = 16.dp
        val Modal: Dp = 24.dp
    }

    object Animation {
        const val DurationFast = 200
        const val DurationNormal = 350
        const val DurationSlow = 600
        const val DurationPulse = 2400
        const val DurationMeshMovement = 8000
    }
}
