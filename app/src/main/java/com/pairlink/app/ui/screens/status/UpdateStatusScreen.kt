package com.pairlink.app.ui.screens.status

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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Gif
import androidx.compose.material.icons.filled.Work
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pairlink.app.core.designsystem.DesignTokens
import com.pairlink.app.core.util.StickerStorageHelper
import com.pairlink.app.domain.model.DefaultStickerPacks
import com.pairlink.app.domain.model.StatusItem
import com.pairlink.app.domain.model.StickerItem
import com.pairlink.app.ui.components.AnimatedMeshBackground
import com.pairlink.app.ui.components.AnimatedStickerImage
import com.pairlink.app.ui.components.BottomNavTab
import com.pairlink.app.ui.components.GlassBottomNavigation
import com.pairlink.app.ui.components.GlassCard
import com.pairlink.app.ui.components.GlassTextField
import com.pairlink.app.ui.components.GlassTopBar
import com.pairlink.app.ui.components.GradientButton

enum class StatusTypeTab {
    PRESETS, ANIMATED_STICKERS
}

/**
 * Update Status Screen with preset activity options, animated GIF stickers, 
 * and multi-select WhatsApp sticker file imports.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UpdateStatusScreen(
    currentStatus: String,
    currentStatusStickerUrl: String = "",
    lastUpdated: String,
    availableStatuses: List<StatusItem>,
    onUpdateStatus: (String) -> Unit,
    onSaveStatusSticker: (StickerItem) -> Unit = {},
    onAddCustomStatus: (String) -> Unit = {},
    onDeleteCustomStatus: (String) -> Unit = {},
    onNavigateBack: () -> Unit,
    currentTab: BottomNavTab,
    onTabSelected: (BottomNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(if (currentStatusStickerUrl.isNotBlank()) StatusTypeTab.ANIMATED_STICKERS else StatusTypeTab.PRESETS) }
    var selectedStatus by remember(currentStatus) { mutableStateOf(currentStatus) }
    var customStatusInput by remember { mutableStateOf("") }

    // Persistent custom imported stickers list
    val customStickers = remember { mutableStateListOf<StickerItem>() }
    LaunchedEffect(Unit) {
        val loaded = StickerStorageHelper.loadImportedStickers(context)
        customStickers.clear()
        customStickers.addAll(loaded)
    }

    var selectedSticker by remember {
        mutableStateOf<StickerItem?>(
            DefaultStickerPacks.find { it.urlOrRes == currentStatusStickerUrl }
                ?: if (currentStatusStickerUrl.isNotBlank()) StickerItem("custom_status_init", "Custom WhatsApp", "WhatsApp Custom", currentStatusStickerUrl, "🎭") else null
        )
    }

    // Phone Storage / WhatsApp Multi-File Picker Launcher for GIF & WebP Stickers (System Files Browser)
    val stickerPickerLauncher = rememberLauncherForActivityResult(
        contract = com.pairlink.app.core.util.StickerPickerContract()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            val newlySaved = StickerStorageHelper.saveImportedStickers(context, uris)
            customStickers.clear()
            customStickers.addAll(newlySaved)
            if (newlySaved.isNotEmpty()) {
                selectedSticker = newlySaved.first()
            }
            selectedTab = StatusTypeTab.ANIMATED_STICKERS
        }
    }

    AnimatedMeshBackground(modifier = modifier) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                GlassTopBar(
                    title = "Update Status",
                    showBack = true,
                    onBackClick = onNavigateBack
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Header Subtitle
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Let them know what you're up to.",
                            color = DesignTokens.Colors.TextSecondary,
                            fontSize = 14.sp
                        )
                    }

                    // Current Status Display Card
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(DesignTokens.Radius.SuperLarge),
                        contentPadding = 24.dp
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(76.dp)
                                    .shadow(16.dp, CircleShape, spotColor = DesignTokens.Colors.PrimaryPink)
                                    .clip(CircleShape)
                                    .background(DesignTokens.Colors.PrimaryPink.copy(alpha = 0.20f))
                                    .border(1.5.dp, DesignTokens.Colors.PrimaryPink.copy(alpha = 0.4f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (selectedSticker != null && selectedSticker?.urlOrRes?.isNotBlank() == true) {
                                    AnimatedStickerImage(
                                        stickerUrl = selectedSticker!!.urlOrRes,
                                        emojiFallback = "📍",
                                        modifier = Modifier.size(64.dp)
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Work,
                                        contentDescription = "Status",
                                        tint = DesignTokens.Colors.PrimaryPink,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                            }

                            Text(
                                text = "Currently $selectedStatus",
                                color = DesignTokens.Colors.PrimaryPink,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = "Updated $lastUpdated",
                                color = DesignTokens.Colors.TextSecondary,
                                fontSize = 13.sp
                            )
                        }
                    }

                    // Tab Selector Switcher (Preset Statuses vs GIF/WhatsApp Stickers)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(DesignTokens.Radius.Large))
                            .background(Color.White.copy(alpha = 0.15f))
                            .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(DesignTokens.Radius.Large))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Presets Tab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clip(RoundedCornerShape(DesignTokens.Radius.Medium))
                                .background(
                                    if (selectedTab == StatusTypeTab.PRESETS) DesignTokens.Colors.PrimaryCrimson
                                    else Color.Transparent
                                )
                                .clickable { selectedTab = StatusTypeTab.PRESETS },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Work,
                                    contentDescription = "Status Presets",
                                    tint = if (selectedTab == StatusTypeTab.PRESETS) Color.White else DesignTokens.Colors.TextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Status Presets",
                                    color = if (selectedTab == StatusTypeTab.PRESETS) Color.White else DesignTokens.Colors.TextSecondary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Animated Stickers Tab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clip(RoundedCornerShape(DesignTokens.Radius.Medium))
                                .background(
                                    if (selectedTab == StatusTypeTab.ANIMATED_STICKERS) DesignTokens.Colors.PrimaryCrimson
                                    else Color.Transparent
                                )
                                .clickable { selectedTab = StatusTypeTab.ANIMATED_STICKERS },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Gif,
                                    contentDescription = "WhatsApp Stickers",
                                    tint = if (selectedTab == StatusTypeTab.ANIMATED_STICKERS) Color.White else DesignTokens.Colors.TextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "GIF Stickers",
                                    color = if (selectedTab == StatusTypeTab.ANIMATED_STICKERS) Color.White else DesignTokens.Colors.TextSecondary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // TAB 1: PRESET STATUS OPTIONS
                    if (selectedTab == StatusTypeTab.PRESETS) {
                        Text(
                            text = "STATUS OPTIONS",
                            color = DesignTokens.Colors.TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        // Grid of Status Options
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            availableStatuses.chunked(2).forEach { rowItems ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    rowItems.forEach { item ->
                                        val isSelected = selectedStatus.equals(item.label, ignoreCase = true) && selectedSticker == null
                                        Box(modifier = Modifier.weight(1f)) {
                                            GlassCard(
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(DesignTokens.Radius.Medium),
                                                contentPadding = 14.dp,
                                                backgroundColor = if (isSelected) DesignTokens.Colors.PrimaryPink.copy(alpha = 0.25f)
                                                else Color.White.copy(alpha = 0.10f),
                                                borderColor = if (isSelected) DesignTokens.Colors.PrimaryPink.copy(alpha = 0.7f)
                                                else Color.White.copy(alpha = 0.15f),
                                                onClick = {
                                                    selectedStatus = item.label
                                                    selectedSticker = null
                                                    onUpdateStatus(item.label)
                                                }
                                            ) {
                                                Column(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = item.icon,
                                                        contentDescription = item.label,
                                                        tint = if (isSelected) DesignTokens.Colors.PrimaryPink else DesignTokens.Colors.TextPrimary,
                                                        modifier = Modifier.size(28.dp)
                                                    )
                                                    Text(
                                                        text = item.label,
                                                        color = if (isSelected) DesignTokens.Colors.PrimaryPink else DesignTokens.Colors.TextPrimary,
                                                        fontSize = 13.sp,
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                                    )
                                                }
                                            }

                                            // Custom status delete icon
                                            if (item.isCustom) {
                                                Box(
                                                    modifier = Modifier
                                                        .align(Alignment.TopEnd)
                                                        .padding(6.dp)
                                                        .size(22.dp)
                                                        .clip(CircleShape)
                                                        .background(Color.White.copy(alpha = 0.20f))
                                                        .clickable {
                                                            onDeleteCustomStatus(item.id)
                                                            if (selectedStatus.equals(item.label, ignoreCase = true)) {
                                                                selectedStatus = "Available"
                                                            }
                                                        },
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Close,
                                                        contentDescription = "Delete Status",
                                                        tint = DesignTokens.Colors.DangerRose,
                                                        modifier = Modifier.size(12.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    if (rowItems.size == 1) {
                                        Box(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }

                        // Custom Status Input Card
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(DesignTokens.Radius.Large),
                            contentPadding = 20.dp
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    text = "CUSTOM STATUS",
                                    color = DesignTokens.Colors.TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    GlassTextField(
                                        value = customStatusInput,
                                        onValueChange = { customStatusInput = it },
                                        placeholder = "What's on your mind?",
                                        modifier = Modifier.weight(1f)
                                    )
                                    GradientButton(
                                        text = "Add",
                                        onClick = {
                                            if (customStatusInput.isNotBlank()) {
                                                val text = customStatusInput.trim()
                                                selectedStatus = text
                                                selectedSticker = null
                                                onAddCustomStatus(text)
                                                customStatusInput = ""
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    } else {
                        // TAB 2: ANIMATED GIF & WHATSAPP STICKERS FOR STATUS
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Multi-Select WhatsApp / Phone Sticker Upload Card
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
                                            contentDescription = "Import WhatsApp Stickers",
                                            tint = Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Upload Multiple WhatsApp Stickers",
                                            color = DesignTokens.Colors.TextPrimary,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Select multiple .gif or .webp sticker files (Saved permanently)",
                                            color = DesignTokens.Colors.TextSecondary,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }

                            // Sticker Packs Grid
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
                                                        selectedStatus = sticker.name
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

                    // Save Status / Sticker Button
                    if (selectedTab == StatusTypeTab.ANIMATED_STICKERS && selectedSticker != null) {
                        GradientButton(
                            text = "Save Animated Status Sticker",
                            onClick = {
                                val activeSticker = selectedSticker!!
                                onSaveStatusSticker(activeSticker)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Extra bottom space
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
