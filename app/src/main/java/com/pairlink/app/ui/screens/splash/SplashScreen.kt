package com.pairlink.app.ui.screens.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pairlink.app.core.designsystem.DesignTokens
import com.pairlink.app.ui.components.AnimatedMeshBackground
import kotlinx.coroutines.delay

/**
 * Splash Screen with animated brand heart, tagline, and comprehensive session restoration.
 */
@Composable
fun SplashScreen(
    onRestoreSession: suspend () -> Boolean = { false },
    onSplashComplete: (isLoggedIn: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var startAnimation by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.4f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "SplashScale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "SplashAlpha"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        val isRestored = onRestoreSession()
        delay(1200)
        onSplashComplete(isRestored)
    }

    AnimatedMeshBackground(modifier = modifier) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .scale(scale)
                    .padding(32.dp)
            ) {
                // Glowing Heart Icon Box
                Box(
                    modifier = Modifier
                        .size(108.dp)
                        .shadow(
                            elevation = 28.dp,
                            shape = CircleShape,
                            ambientColor = DesignTokens.Colors.PrimaryPink.copy(alpha = 0.5f),
                            spotColor = DesignTokens.Colors.SoftPink
                        )
                        .clip(CircleShape)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.White.copy(alpha = 0.25f),
                                    Color.White.copy(alpha = 0.10f)
                                )
                            )
                        )
                        .border(1.5.dp, Color.White.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "PairLink Logo",
                        tint = DesignTokens.Colors.PrimaryPink,
                        modifier = Modifier.size(54.dp)
                    )
                }

                // App Title
                Text(
                    text = "PairLink",
                    color = DesignTokens.Colors.PrimaryPink,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                )

                // Tagline
                Text(
                    text = "One App. One Partner. One Connection.",
                    color = DesignTokens.Colors.TextSecondary.copy(alpha = alpha),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}
