package com.pairlink.app.ui.screens.pairing

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.pairlink.app.core.designsystem.DesignTokens
import com.pairlink.app.domain.model.PublicPartnerPreview
import com.pairlink.app.ui.components.AnimatedMeshBackground
import com.pairlink.app.ui.components.GlassButton
import com.pairlink.app.ui.components.GlassCard
import com.pairlink.app.ui.components.GlassTextField
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Connect Partner Screen with own Partner ID card, live search, partner preview, and validation.
 */
@Composable
fun ConnectScreen(
    myPartnerId: String = "",
    partnerPreview: PublicPartnerPreview? = null,
    onSearchPartner: (partnerId: String) -> Unit = {},
    onConnectPartner: (partnerId: String) -> Unit,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onClearError: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var targetId by remember { mutableStateOf("") }
    var isCopied by remember { mutableStateOf(false) }

    fun copyMyPartnerId() {
        if (myPartnerId.isBlank()) return
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("PairLink Partner ID", myPartnerId)
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
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // Header
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Connect",
                        color = DesignTokens.Colors.PrimaryPink,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "With Your Partner",
                        color = DesignTokens.Colors.TextSecondary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Error Banner
                AnimatedVisibility(
                    visible = !errorMessage.isNullOrBlank(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(DesignTokens.Radius.Medium))
                            .background(DesignTokens.Colors.DangerRoseSurface)
                            .border(1.dp, DesignTokens.Colors.DangerRose.copy(alpha = 0.5f), RoundedCornerShape(DesignTokens.Radius.Medium))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = "Error",
                            tint = DesignTokens.Colors.DangerRose,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = errorMessage ?: "",
                            color = Color(0xFFFFE4E6),
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }

                // Card 1: Your Unique Partner ID (Shareable)
                if (myPartnerId.isNotBlank()) {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(DesignTokens.Radius.Large),
                        contentPadding = 16.dp
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "YOUR PARTNER ID",
                                color = DesignTokens.Colors.TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = myPartnerId,
                                    color = DesignTokens.Colors.TextPrimary,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.5.sp
                                )

                                Box(modifier = Modifier.padding(start = 12.dp))

                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
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
                                        .clickable(onClick = ::copyMyPartnerId),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                                        contentDescription = "Copy ID",
                                        tint = if (isCopied) DesignTokens.Colors.OnlineGreen else DesignTokens.Colors.PrimaryPink,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Text(
                                text = if (isCopied) "Copied to clipboard! Share it with your partner." else "Share this ID with your partner so they can connect with you.",
                                color = if (isCopied) DesignTokens.Colors.OnlineGreen else DesignTokens.Colors.TextSecondary.copy(alpha = 0.8f),
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // Pairing Form Glass Card
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(DesignTokens.Radius.ExtraLarge),
                    contentPadding = 20.dp
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "ENTER PARTNER'S ID",
                            color = DesignTokens.Colors.TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        GlassTextField(
                            value = targetId,
                            onValueChange = {
                                targetId = it.uppercase()
                                onClearError()
                            },
                            placeholder = "e.g. PAIR-XXXXXXXX",
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.VpnKey,
                                    contentDescription = "Partner Key",
                                    tint = DesignTokens.Colors.TextSecondary.copy(alpha = 0.7f),
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search Partner",
                                    tint = if (targetId.isNotBlank()) DesignTokens.Colors.PrimaryPink else DesignTokens.Colors.TextSecondary.copy(alpha = 0.5f),
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clickable {
                                            if (targetId.isNotBlank()) {
                                                onSearchPartner(targetId.trim())
                                            }
                                        }
                                )
                            }
                        )

                        // Partner Preview Card
                        if (partnerPreview != null) {
                            GlassCard(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(DesignTokens.Radius.Medium),
                                contentPadding = 14.dp,
                                backgroundColor = DesignTokens.Colors.PrimaryPink.copy(alpha = 0.12f),
                                borderColor = DesignTokens.Colors.PrimaryPink.copy(alpha = 0.40f)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    if (partnerPreview.profileImageUrl.isNotBlank()) {
                                        AsyncImage(
                                            model = com.pairlink.app.core.util.ImageUtils.getAvatarModel(partnerPreview.profileImageUrl),
                                            contentDescription = partnerPreview.username,
                                            modifier = Modifier
                                                .size(52.dp)
                                                .clip(CircleShape)
                                                .border(1.5.dp, DesignTokens.Colors.PrimaryPink, CircleShape),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .size(52.dp)
                                                .clip(CircleShape)
                                                .background(DesignTokens.Colors.PrimaryPink.copy(alpha = 0.3f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = partnerPreview.username.take(1).uppercase(),
                                                color = Color.White,
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = partnerPreview.username,
                                            color = DesignTokens.Colors.TextPrimary,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = partnerPreview.partnerId,
                                            color = DesignTokens.Colors.PrimaryPink,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = if (partnerPreview.isAlreadyPaired) "Already Paired" else "Available to Connect",
                                            color = if (partnerPreview.isAlreadyPaired) DesignTokens.Colors.DangerRose else Color(0xFF86EFAC),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Valid",
                                        tint = if (partnerPreview.isAlreadyPaired) DesignTokens.Colors.DangerRose else Color(0xFF86EFAC),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }

                        // Connect Button
                        GlassButton(
                            text = if (isLoading) "Connecting..." else if (partnerPreview != null) "Send Connection Request" else "Search & Connect",
                            enabled = !isLoading && targetId.isNotBlank() && (partnerPreview == null || !partnerPreview.isAlreadyPaired),
                            onClick = {
                                if (targetId.isNotBlank()) {
                                    if (partnerPreview != null && partnerPreview.partnerId.equals(targetId.trim(), ignoreCase = true)) {
                                        onConnectPartner(partnerPreview.partnerId)
                                    } else {
                                        onSearchPartner(targetId.trim())
                                        onConnectPartner(targetId.trim())
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = if (isLoading) {
                                {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        color = DesignTokens.Colors.PrimaryPink,
                                        strokeWidth = 2.dp
                                    )
                                }
                            } else null,
                            trailingIcon = if (!isLoading) {
                                {
                                    Icon(
                                        imageVector = Icons.Default.Favorite,
                                        contentDescription = "Connect",
                                        tint = DesignTokens.Colors.PrimaryPink,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            } else null
                        )
                    }
                }
            }
        }
    }
}
