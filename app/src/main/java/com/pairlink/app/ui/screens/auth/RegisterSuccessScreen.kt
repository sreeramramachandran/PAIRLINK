package com.pairlink.app.ui.screens.auth

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pairlink.app.core.designsystem.DesignTokens
import com.pairlink.app.ui.components.AnimatedMeshBackground
import com.pairlink.app.ui.components.GlassCard
import com.pairlink.app.ui.components.GradientButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Registration Success Screen displaying newly generated Partner ID and copy utility.
 */
@Composable
fun RegisterSuccessScreen(
    username: String,
    partnerId: String,
    onNavigateToConnect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isCopied by remember { mutableStateOf(false) }

    fun copyPartnerId() {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("PairLink Partner ID", partnerId)
        clipboard.setPrimaryClip(clip)
        isCopied = true
        coroutineScope.launch {
            delay(2000)
            isCopied = false
        }
    }

    AnimatedMeshBackground(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Celebration Icon Box
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .shadow(20.dp, CircleShape, spotColor = DesignTokens.Colors.PrimaryPink)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                        .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Success",
                        tint = DesignTokens.Colors.PrimaryPink,
                        modifier = Modifier.size(44.dp)
                    )
                }

                // Greeting Title
                Text(
                    text = "Welcome, ${username.ifBlank { "Partner" }}!",
                    color = DesignTokens.Colors.PrimaryPink,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.4).sp,
                    textAlign = TextAlign.Center
                )

                // Partner ID Glass Card
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(DesignTokens.Radius.ExtraLarge),
                    contentPadding = 24.dp
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "YOUR PARTNER ID",
                            color = DesignTokens.Colors.TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.2.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = partnerId,
                                color = DesignTokens.Colors.TextPrimary,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            )

                            Box(modifier = Modifier.padding(start = 16.dp))

                            // Copy Button
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isCopied) DesignTokens.Colors.OnlineGreen.copy(alpha = 0.25f)
                                        else Color.White.copy(alpha = 0.15f)
                                    )
                                    .border(
                                        1.dp,
                                        if (isCopied) DesignTokens.Colors.OnlineGreen else Color.White.copy(alpha = 0.25f),
                                        CircleShape
                                    )
                                    .clickable(onClick = ::copyPartnerId),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                                    contentDescription = "Copy ID",
                                    tint = if (isCopied) DesignTokens.Colors.OnlineGreen else DesignTokens.Colors.PrimaryPink,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Text(
                            text = "Share this secure code with your partner to link your sanctuaries.",
                            color = DesignTokens.Colors.TextSecondary.copy(alpha = 0.8f),
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )
                    }
                }

                // CTA Button
                GradientButton(
                    text = "Connect With Partner",
                    onClick = onNavigateToConnect,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Connect",
                            tint = DesignTokens.Colors.DeepPinkText,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )
            }
        }
    }
}
