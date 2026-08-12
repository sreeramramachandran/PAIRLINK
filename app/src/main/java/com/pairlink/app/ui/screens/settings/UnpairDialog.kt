package com.pairlink.app.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HeartBroken
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pairlink.app.core.designsystem.DesignTokens
import com.pairlink.app.ui.components.GlassButton
import com.pairlink.app.ui.components.GlassCard
import com.pairlink.app.ui.components.GlassDialog

/**
 * Unpair Confirmation Dialog matching the Stitch prototype.
 */
@Composable
fun UnpairDialog(
    onConfirmUnpair: () -> Unit,
    onCancel: () -> Unit
) {
    GlassDialog(onDismissRequest = onCancel) {
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(DesignTokens.Radius.SuperLarge),
            contentPadding = 28.dp
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // Broken Heart Icon
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .shadow(16.dp, CircleShape, spotColor = DesignTokens.Colors.DangerRose)
                        .clip(CircleShape)
                        .background(DesignTokens.Colors.DangerRoseSurface)
                        .border(1.5.dp, DesignTokens.Colors.DangerRose.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.HeartBroken,
                        contentDescription = "Unpair",
                        tint = DesignTokens.Colors.DangerRose,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Title and Warning
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Unpair?",
                        color = DesignTokens.Colors.TextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "This will permanently remove your connection with your partner.",
                        color = DesignTokens.Colors.TextSecondary,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                }

                // Actions
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GlassButton(
                        text = "Unpair",
                        onClick = onConfirmUnpair,
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = DesignTokens.Colors.DangerRose.copy(alpha = 0.35f),
                        borderColor = DesignTokens.Colors.DangerRose.copy(alpha = 0.6f),
                        textColor = Color(0xFFFFE4E6)
                    )

                    GlassButton(
                        text = "Cancel",
                        onClick = onCancel,
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = Color.White.copy(alpha = 0.08f),
                        textColor = DesignTokens.Colors.TextPrimary
                    )
                }
            }
        }
    }
}
