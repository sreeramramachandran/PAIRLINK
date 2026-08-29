package com.pairlink.app.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pairlink.app.R
import com.pairlink.app.core.designsystem.DesignTokens

@Composable
fun PairLinkLogo(
    modifier: Modifier = Modifier,
    logoSize: Dp = 80.dp,
    showTagline: Boolean = true,
    iconOnly: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "logoPulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Official 3D Interlocking Heart Logo
        Box(
            modifier = Modifier
                .size(logoSize)
                .graphicsLayer(scaleX = scale, scaleY = scale),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_app_logo),
                contentDescription = "PairLink Logo",
                modifier = Modifier.size(logoSize)
            )
        }

        if (!iconOnly) {
            Spacer(modifier = Modifier.height(8.dp))

            // PairLink Title
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = DesignTokens.Colors.TextPrimary, fontWeight = FontWeight.ExtraBold)) {
                        append("Pair")
                    }
                    withStyle(style = SpanStyle(color = DesignTokens.Colors.PrimaryCrimson, fontWeight = FontWeight.ExtraBold)) {
                        append("Link")
                    }
                },
                fontSize = if (logoSize > 80.dp) 32.sp else 24.sp,
                letterSpacing = (-0.5).sp
            )

            if (showTagline) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "♥ Distance can't break us ♥",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = DesignTokens.Colors.TextSecondary
                    )
                }
            }
        }
    }
}
