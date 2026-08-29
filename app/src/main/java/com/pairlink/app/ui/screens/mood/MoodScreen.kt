package com.pairlink.app.ui.screens.mood

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Gif
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pairlink.app.core.designsystem.DesignTokens
import com.pairlink.app.domain.model.DefaultStickerPacks
import com.pairlink.app.domain.model.MoodItem
import com.pairlink.app.domain.model.StickerItem
import com.pairlink.app.ui.components.AnimatedMeshBackground
import com.pairlink.app.ui.components.AnimatedStickerImage
import com.pairlink.app.ui.components.BottomNavTab
import com.pairlink.app.ui.components.GlassBottomNavigation
import com.pairlink.app.ui.components.GlassCard
import com.pairlink.app.ui.components.GlassTextField
import com.pairlink.app.ui.components.GlassTopBar
import com.pairlink.app.ui.components.GradientButton
import com.pairlink.app.ui.components.MoodChip

enum class MoodTypeTab {
    EMOJI, ANIMATED_STICKERS
}

/**
 * Enhanced Mood Screen allowing user to select emoji moods, custom moods, 
 * animated GIF stickers, or import custom WhatsApp stickers from phone storage.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MoodScreen(
    currentMood: String,
    currentStickerUrl: String = "",
    availableMoods: List<MoodItem>,
    onSaveMood: (String) -> Unit,
    onSaveStickerMood: (StickerItem) -> Unit = {},
    onDeleteMood: (String) -> Unit = {},
    onNavigateBack: () -> Unit,
    currentTab: BottomNavTab,
    onTabSelected: (BottomNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(if (currentStickerUrl.isNotBlank()) MoodTypeTab.ANIMATED_STICKERS else MoodTypeTab.EMOJI) }
    var selectedMood by remember(currentMood) { mutableStateOf(currentMood) }
    var customMoodText by remember { mutableStateOf("") }
    
    val context = androidx.compose.ui.platform.LocalContext.current

    // Custom imported stickers list loaded persistently from app storage
    val customStickers = remember { mutableStateListOf<StickerItem>() }
    
    androidx.compose.runtime.LaunchedEffect(Unit) {
        val loaded = com.pairlink.app.core.util.StickerStorageHelper.loadImportedStickers(context)
        customStickers.clear()
        customStickers.addAll(loaded)
    }

    var selectedSticker by remember { 
        mutableStateOf<StickerItem?>(
            DefaultStickerPacks.find { it.urlOrRes == currentStickerUrl } 
                ?: if (currentStickerUrl.isNotBlank()) StickerItem("custom_init", "Custom WhatsApp", "WhatsApp Custom", currentStickerUrl, "🎭") else null
        )
    }

    // Phone Storage / WhatsApp Multi-File Picker Launcher for GIF & WebP Stickers (System Files Browser)
    val stickerPickerLauncher = rememberLauncherForActivityResult(
        contract = com.pairlink.app.core.util.StickerPickerContract()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            val newlySaved = com.pairlink.app.core.util.StickerStorageHelper.saveImportedStickers(context, uris)
            customStickers.clear()
            customStickers.addAll(newlySaved)
            if (newlySaved.isNotEmpty()) {
                selectedSticker = newlySaved.first()
            }
            selectedTab = MoodTypeTab.ANIMATED_STICKERS
        }
    }

    AnimatedMeshBackground(modifier = modifier) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                GlassTopBar(
                    title = "Mood Sanctuary",
                    showBack = true,
                    onBackClick = onNavigateBack
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Header Question
                    Text(
                        text = "How are you feeling?",
                        color = DesignTokens.Colors.TextPrimary,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    // Mood Mode Segmented Glass Tab Switcher (Emoji vs Animated Stickers)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(DesignTokens.Radius.Large))
                            .background(Color.White.copy(alpha = 0.15f))
                            .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(DesignTokens.Radius.Large))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Emoji Tab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clip(RoundedCornerShape(DesignTokens.Radius.Medium))
                                .background(
                                    if (selectedTab == MoodTypeTab.EMOJI) DesignTokens.Colors.PrimaryCrimson
                                    else Color.Transparent
                                )
                                .clickable { selectedTab = MoodTypeTab.EMOJI },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EmojiEmotions,
                                    contentDescription = "Emoji Moods",
                                    tint = if (selectedTab == MoodTypeTab.EMOJI) Color.White else DesignTokens.Colors.TextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Emoji Moods",
                                    color = if (selectedTab == MoodTypeTab.EMOJI) Color.White else DesignTokens.Colors.TextSecondary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Animated GIF Stickers Tab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clip(RoundedCornerShape(DesignTokens.Radius.Medium))
                                .background(
                                    if (selectedTab == MoodTypeTab.ANIMATED_STICKERS) DesignTokens.Colors.PrimaryCrimson
                                    else Color.Transparent
                                )
                                .clickable { selectedTab = MoodTypeTab.ANIMATED_STICKERS },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Gif,
                                    contentDescription = "WhatsApp Stickers",
                                    tint = if (selectedTab == MoodTypeTab.ANIMATED_STICKERS) Color.White else DesignTokens.Colors.TextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "GIF Stickers",
                                    color = if (selectedTab == MoodTypeTab.ANIMATED_STICKERS) Color.White else DesignTokens.Colors.TextSecondary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // CONTENT TAB 1: EMOJI MOODS
                    if (selectedTab == MoodTypeTab.EMOJI) {
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            maxItemsInEachRow = 3
                        ) {
                            availableMoods.forEach { item ->
                                val isSelected = selectedMood.equals(item.label, ignoreCase = true) && customMoodText.isBlank()
                                MoodChip(
                                    mood = item,
                                    selected = isSelected,
                                    onClick = {
                                        selectedMood = item.label
                                        customMoodText = ""
                                        selectedSticker = null
                                    },
                                    onDelete = {
                                        onDeleteMood(item.id)
                                        if (selectedMood.equals(item.label, ignoreCase = true)) {
                                            selectedMood = "Happy"
                                        }
                                    },
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                )
                            }
                        }

                        // Custom Mood Input
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "CUSTOM EMOJI MOOD",
                                color = DesignTokens.Colors.TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            GlassTextField(
                                value = customMoodText,
                                onValueChange = {
                                    customMoodText = it
                                    if (it.isNotBlank()) selectedMood = it
                                },
                                placeholder = "Create Custom Mood...",
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Custom Mood",
                                        tint = DesignTokens.Colors.TextSecondary.copy(alpha = 0.6f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            )
                        }
                    } else {
                        // CONTENT TAB 2: ANIMATED GIF & WHATSAPP STICKERS
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Import WhatsApp / Custom Sticker Action Card
                            GlassCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { stickerPickerLauncher.launch(Unit) },
                                shape = RoundedCornerShape(DesignTokens.Radius.Medium),
                                contentPadding = 14.dp,
                                backgroundColor = DesignTokens.Colors.PrimaryCrimson.copy(alpha = 0.10f),
                                borderColor = DesignTokens.Colors.PrimaryCrimson.copy(alpha = 0.35f)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(DesignTokens.Colors.PrimaryCrimson),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Import WhatsApp Sticker",
                                            tint = Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Import WhatsApp / Phone Sticker",
                                            color = DesignTokens.Colors.TextPrimary,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Select any .gif or .webp WhatsApp sticker file",
                                            color = DesignTokens.Colors.TextSecondary,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }

                            // Sticker Packs Grouped
                            val allStickers = customStickers + DefaultStickerPacks
                            val groupedPacks = allStickers.groupBy { it.packName }

                            groupedPacks.forEach { (packName, stickersInPack) ->
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = packName.uppercase(),
                                        color = DesignTokens.Colors.TextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.2.sp
                                    )

                                    FlowRow(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        verticalArrangement = Arrangement.spacedBy(10.dp),
                                        maxItemsInEachRow = 3
                                    ) {
                                        stickersInPack.forEach { sticker ->
                                            val isSelected = selectedSticker?.id == sticker.id

                                            Box(
                                                modifier = Modifier
                                                    .size(96.dp)
                                                    .clip(RoundedCornerShape(DesignTokens.Radius.Medium))
                                                    .background(
                                                        if (isSelected) DesignTokens.Colors.PrimaryCrimson.copy(alpha = 0.20f)
                                                        else Color.White.copy(alpha = 0.12f)
                                                    )
                                                    .border(
                                                        1.5.dp,
                                                        if (isSelected) DesignTokens.Colors.PrimaryCrimson else Color.White.copy(alpha = 0.25f),
                                                        RoundedCornerShape(DesignTokens.Radius.Medium)
                                                    )
                                                    .clickable {
                                                        selectedSticker = sticker
                                                        selectedMood = sticker.name
                                                    }
                                                    .padding(8.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Column(
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    verticalArrangement = Arrangement.Center
                                                ) {
                                                    AnimatedStickerImage(
                                                        stickerUrl = sticker.urlOrRes,
                                                        emojiFallback = sticker.emojiFallback,
                                                        modifier = Modifier.size(56.dp)
                                                    )
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(
                                                        text = sticker.name,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = DesignTokens.Colors.TextPrimary,
                                                        maxLines = 1
                                                    )
                                                }

                                                if (isSelected) {
                                                    Box(
                                                        modifier = Modifier
                                                            .align(Alignment.TopEnd)
                                                            .size(20.dp)
                                                            .clip(CircleShape)
                                                            .background(DesignTokens.Colors.PrimaryCrimson),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Check,
                                                            contentDescription = "Selected",
                                                            tint = Color.White,
                                                            modifier = Modifier.size(12.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Save Mood Button
                    GradientButton(
                        text = if (selectedTab == MoodTypeTab.ANIMATED_STICKERS && selectedSticker != null) 
                            "Save Animated Sticker Mood" else "Save Mood",
                        onClick = {
                            val activeSticker = selectedSticker
                            if (selectedTab == MoodTypeTab.ANIMATED_STICKERS && activeSticker != null) {
                                onSaveStickerMood(activeSticker)
                            } else {
                                val finalMood = customMoodText.trim().ifBlank { selectedMood }
                                onSaveMood(finalMood)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Extra bottom padding
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
