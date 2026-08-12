package com.pairlink.app.ui.screens.pairing

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.pairlink.app.core.designsystem.DesignTokens
import com.pairlink.app.core.util.ImageUtils
import com.pairlink.app.ui.components.AnimatedMeshBackground
import com.pairlink.app.ui.components.GlassButton

/**
 * Connected Forever Screen displaying linked avatars and celebration.
 */
@Composable
fun ConnectedForeverScreen(
    userAvatar: String,
    partnerAvatar: String,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
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
                verticalArrangement = Arrangement.spacedBy(36.dp)
            ) {
                // Title
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Connected Forever",
                        color = DesignTokens.Colors.PrimaryPink,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "Your sanctuary is ready.",
                        color = DesignTokens.Colors.TextSecondary,
                        fontSize = 16.sp
                    )
                }

                // Interlocked Avatars Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Left Partner Avatar
                    val partnerModel = ImageUtils.getAvatarModel(partnerAvatar)
                    if (partnerModel != null) {
                        AsyncImage(
                            model = partnerModel,
                            contentDescription = "Partner Avatar",
                            modifier = Modifier
                                .offset(x = (-42).dp)
                                .size(118.dp)
                                .rotate(-6f)
                                .shadow(24.dp, CircleShape, spotColor = DesignTokens.Colors.SoftPink)
                                .clip(CircleShape)
                                .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .offset(x = (-42).dp)
                                .size(118.dp)
                                .rotate(-6f)
                                .shadow(24.dp, CircleShape, spotColor = DesignTokens.Colors.SoftPink)
                                .clip(CircleShape)
                                .background(DesignTokens.Colors.SoftPink.copy(alpha = 0.35f))
                                .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Partner",
                                tint = Color.White,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }

                    // Right User Avatar
                    val userModel = ImageUtils.getAvatarModel(userAvatar)
                    if (userModel != null) {
                        AsyncImage(
                            model = userModel,
                            contentDescription = "User Avatar",
                            modifier = Modifier
                                .offset(x = 42.dp)
                                .size(118.dp)
                                .rotate(6f)
                                .shadow(24.dp, CircleShape, spotColor = DesignTokens.Colors.Lavender)
                                .clip(CircleShape)
                                .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .offset(x = 42.dp)
                                .size(118.dp)
                                .rotate(6f)
                                .shadow(24.dp, CircleShape, spotColor = DesignTokens.Colors.Lavender)
                                .clip(CircleShape)
                                .background(DesignTokens.Colors.Lavender.copy(alpha = 0.35f))
                                .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "User",
                                tint = Color.White,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }

                    // Center Heart Badge
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .shadow(16.dp, CircleShape, spotColor = DesignTokens.Colors.PrimaryPink)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.20f))
                            .border(1.5.dp, Color.White.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Heart Link",
                            tint = DesignTokens.Colors.PrimaryPink,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                // Continue Action Button
                GlassButton(
                    text = "Continue",
                    onClick = onContinue,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Continue",
                            tint = DesignTokens.Colors.PrimaryPink,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )
            }
        }
    }
}
