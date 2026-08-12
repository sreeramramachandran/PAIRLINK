package com.pairlink.app.ui.screens.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.pairlink.app.core.designsystem.DesignTokens
import com.pairlink.app.domain.model.UserProfile
import com.pairlink.app.ui.components.AnimatedMeshBackground
import com.pairlink.app.ui.components.BottomNavTab
import com.pairlink.app.ui.components.GlassBottomNavigation
import com.pairlink.app.ui.components.GlassButton
import com.pairlink.app.ui.components.GlassCard
import com.pairlink.app.ui.components.GlassTextField
import com.pairlink.app.ui.components.GlassTopBar

/**
 * Clean & intuitive Edit Profile screen.
 * Allows user to update:
 * 1. Their OWN profile photo
 * 2. Their OWN username
 * 3. Their custom Nickname for their partner
 *
 * Birthday is permanent (set at registration) and read-only.
 */
@Composable
fun EditProfileScreen(
    user: UserProfile,
    onSaveProfile: (username: String, nickname: String, avatarUri: Uri?) -> Unit,
    onNavigateBack: () -> Unit,
    isLoading: Boolean = false,
    currentTab: BottomNavTab = BottomNavTab.SETTINGS,
    onTabSelected: (BottomNavTab) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var username by remember(user.username) { mutableStateOf(user.username) }
    var partnerNickname by remember(user.partnerNickname) { mutableStateOf(user.partnerNickname) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Image Picker Launcher for the user's OWN photo
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    AnimatedMeshBackground(modifier = modifier) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                GlassTopBar(
                    title = "Profile & Nickname",
                    showBack = true,
                    onBackClick = onNavigateBack
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .imePadding()
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    // Header
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Your Profile",
                            color = DesignTokens.Colors.TextPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Manage your photo and partner nickname",
                            color = DesignTokens.Colors.TextSecondary,
                            fontSize = 13.sp
                        )
                    }

                    // Card 1: Your Own Avatar Upload
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(DesignTokens.Radius.Large),
                        contentPadding = 20.dp
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "YOUR PROFILE PHOTO",
                                color = DesignTokens.Colors.TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            )

                            Box(
                                modifier = Modifier.size(110.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .shadow(16.dp, CircleShape, spotColor = DesignTokens.Colors.PrimaryPink)
                                        .clip(CircleShape)
                                        .background(DesignTokens.Colors.PrimaryPink.copy(alpha = 0.20f))
                                        .border(2.dp, DesignTokens.Colors.PrimaryPink.copy(alpha = 0.6f), CircleShape)
                                        .clickable { imagePickerLauncher.launch("image/*") },
                                    contentAlignment = Alignment.Center
                                ) {
                                    val currentPhoto = selectedImageUri ?: user.avatarUrl.ifBlank { null }
                                    if (currentPhoto != null) {
                                        AsyncImage(
                                            model = com.pairlink.app.core.util.ImageUtils.getAvatarModel(currentPhoto),
                                            contentDescription = "Your Profile Photo",
                                            modifier = Modifier
                                                .size(100.dp)
                                                .clip(CircleShape),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = "No photo",
                                            tint = DesignTokens.Colors.PrimaryPink,
                                            modifier = Modifier.size(44.dp)
                                        )
                                    }
                                }

                                // Camera overlay badge
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .align(Alignment.BottomEnd)
                                        .clip(CircleShape)
                                        .background(DesignTokens.Colors.PrimaryPink)
                                        .clickable { imagePickerLauncher.launch("image/*") },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CameraAlt,
                                        contentDescription = "Change Photo",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Text(
                                text = if (selectedImageUri != null) "New photo selected (tap Save to upload)" else "Tap to change your profile picture",
                                color = if (selectedImageUri != null) DesignTokens.Colors.OnlineGreen else DesignTokens.Colors.TextSecondary.copy(alpha = 0.7f),
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Card 2: Your Username
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(DesignTokens.Radius.Large),
                        contentPadding = 18.dp
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)

                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Username",
                                    tint = DesignTokens.Colors.PrimaryPink,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "YOUR USERNAME",
                                    color = DesignTokens.Colors.TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }
                            Text(
                                text = user.username.ifBlank { "Set at registration" },
                                color = DesignTokens.Colors.PrimaryPink,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                    }

                    // Card 3: Partner's Nickname (The sweet name you call them)
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(DesignTokens.Radius.Large),
                        contentPadding = 18.dp
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "Partner Nickname",
                                    tint = DesignTokens.Colors.PrimaryPink,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "PARTNER NICKNAME",
                                    color = DesignTokens.Colors.TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }
                            GlassTextField(
                                value = partnerNickname,
                                onValueChange = { partnerNickname = it },
                                placeholder = "e.g. My Love, Sweetheart, Chakkara"
                            )
                            Text(
                                text = "A private special nickname only you see on your screens.",
                                color = DesignTokens.Colors.TextSecondary.copy(alpha = 0.7f),
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Card 4: Date of Birth (Permanent, Read-Only)
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(DesignTokens.Radius.Large),
                        contentPadding = 18.dp
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Cake,
                                    contentDescription = "Birthday",
                                    tint = DesignTokens.Colors.TextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(
                                        text = "YOUR DATE OF BIRTH",
                                        color = DesignTokens.Colors.TextSecondary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = user.dateOfBirth.ifBlank { "Set at registration" },
                                        color = DesignTokens.Colors.TextPrimary,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Locked",
                                    tint = DesignTokens.Colors.TextSecondary.copy(alpha = 0.6f),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Permanent",
                                    color = DesignTokens.Colors.TextSecondary.copy(alpha = 0.6f),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    // Save Button
                    GlassButton(
                        text = if (isLoading) "Saving..." else "Save Changes",
                        enabled = !isLoading && username.isNotBlank(),
                        onClick = {
                            onSaveProfile(username.trim(), partnerNickname.trim(), selectedImageUri)
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
                        } else null
                    )

                    Box(modifier = Modifier.padding(bottom = 80.dp))
                }
            }

            GlassBottomNavigation(
                currentTab = currentTab,
                onTabSelected = onTabSelected,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}
