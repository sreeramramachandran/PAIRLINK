package com.pairlink.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pairlink.app.core.designsystem.DesignTokens
import com.pairlink.app.domain.model.PresenceRingState
import kotlinx.coroutines.delay
import kotlin.random.Random

data class FloatingHeartParticle(
    val id: Long,
    val offsetX: Float,
    val offsetY: Float,
    val size: Float
)

/**
 * Signature Presence Heart Button for PairLink.
 */
@Composable
fun PresenceHeartButton(
    heartbeatCount: Int,
    isPartnerHolding: Boolean = false,
    partnerName: String = "Partner",
    onHeartPressed: () -> Unit = {},
    onHeartReleased: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    val floatingHearts = remember { mutableStateListOf<FloatingHeartParticle>() }

    // Breathing pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "HeartBreathing")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (isPartnerHolding) 1.14f else 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (isPartnerHolding) 600 else DesignTokens.Animation.DurationPulse,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseScale"
    )

    val pressScale by animateFloatAsState(
        targetValue = if (isPressed || isPartnerHolding) 1.12f else 1.0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 500f),
        label = "PressScale"
    )

    // Particle cleanup coroutine
    LaunchedEffect(floatingHearts.size) {
        if (floatingHearts.isNotEmpty()) {
            delay(1500)
            if (floatingHearts.isNotEmpty()) {
                floatingHearts.removeAt(0)
            }
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier.size(120.dp),
            contentAlignment = Alignment.Center
        ) {
            // Render floating particle hearts
            floatingHearts.forEach { particle ->
                Text(
                    text = "❤️",
                    fontSize = particle.size.sp,
                    modifier = Modifier
                        .offset(x = particle.offsetX.dp, y = (particle.offsetY - 40).dp)
                )
            }

            // Glowing Outer Halo
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .scale(pulseScale * (if (isPressed || isPartnerHolding) 1.35f else 1.1f))
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                DesignTokens.Colors.PrimaryCrimson.copy(alpha = if (isPressed || isPartnerHolding) 0.45f else 0.20f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Main Heart Button
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .scale(pressScale * pulseScale)
                    .shadow(
                        elevation = if (isPressed || isPartnerHolding) 20.dp else 10.dp,
                        shape = CircleShape,
                        ambientColor = DesignTokens.Colors.PrimaryCrimson.copy(alpha = 0.4f),
                        spotColor = DesignTokens.Colors.PrimaryCrimson
                    )
                    .clip(CircleShape)
                    .background(DesignTokens.Colors.PrimaryCrimson)
                    .border(2.dp, Color.White, CircleShape)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                isPressed = true
                                onHeartPressed()
                                val particle = FloatingHeartParticle(
                                    id = System.currentTimeMillis(),
                                    offsetX = Random.nextFloat() * 60f - 30f,
                                    offsetY = Random.nextFloat() * -30f,
                                    size = Random.nextFloat() * 10f + 18f
                                )
                                floatingHearts.add(particle)

                                tryAwaitRelease()
                                isPressed = false
                                onHeartReleased()
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Send Love Burst",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        // Subtitle instructions
        if (isPartnerHolding) {
            Text(
                text = "❤️ $partnerName is holding with you...",
                color = DesignTokens.Colors.PrimaryCrimson,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        } else {
            Text(
                text = "PRESS & HOLD",
                color = DesignTokens.Colors.TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )

            if (heartbeatCount > 0) {
                Text(
                    text = "$heartbeatCount heartbeats sent today",
                    color = DesignTokens.Colors.PrimaryCrimson,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Reusable Glowing Presence Ring around Partner Avatar.
 */
@Composable
fun PresenceRing(
    modifier: Modifier = Modifier,
    size: Dp = 128.dp,
    ringState: PresenceRingState = PresenceRingState.ONLINE,
    content: @Composable () -> Unit
) {
    val targetColor = when (ringState) {
        PresenceRingState.OFFLINE -> Color(0xFF9CA3AF)
        PresenceRingState.ONLINE -> DesignTokens.Colors.OnlineGreen
        PresenceRingState.SLEEPING -> Color(0xFF60A5FA)
        PresenceRingState.BUSY -> Color(0xFFFBBF24)
        PresenceRingState.DRIVING -> Color(0xFFFB923C)
        PresenceRingState.HOLDING -> DesignTokens.Colors.PrimaryCrimson
    }

    val animatedColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(durationMillis = 400),
        label = "RingColor"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "PresenceRingGlow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = if (ringState == PresenceRingState.HOLDING) 0.55f else 0.35f,
        targetValue = if (ringState == PresenceRingState.HOLDING) 1.0f else 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (ringState == PresenceRingState.HOLDING) 600 else 2000,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "RingAlpha"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            animatedColor.copy(alpha = glowAlpha * 0.45f),
                            Color.Transparent
                        )
                    )
                )
        )
        Box(
            modifier = Modifier
                .size(size - 8.dp)
                .clip(CircleShape)
                .border(2.5.dp, animatedColor.copy(alpha = glowAlpha), CircleShape)
        )
        content()
    }
}
