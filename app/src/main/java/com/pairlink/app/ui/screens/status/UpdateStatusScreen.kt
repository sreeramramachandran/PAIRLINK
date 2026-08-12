package com.pairlink.app.ui.screens.status

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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Work
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pairlink.app.core.designsystem.DesignTokens
import com.pairlink.app.domain.model.StatusItem
import com.pairlink.app.ui.components.AnimatedMeshBackground
import com.pairlink.app.ui.components.BottomNavTab
import com.pairlink.app.ui.components.GlassBottomNavigation
import com.pairlink.app.ui.components.GlassCard
import com.pairlink.app.ui.components.GlassTextField
import com.pairlink.app.ui.components.GlassTopBar
import com.pairlink.app.ui.components.GradientButton

/**
 * Update Status Screen matching Stitch prototype with custom status creation and deletion.
 */
@Composable
fun UpdateStatusScreen(
    currentStatus: String,
    lastUpdated: String,
    availableStatuses: List<StatusItem>,
    onUpdateStatus: (String) -> Unit,
    onAddCustomStatus: (String) -> Unit = {},
    onDeleteCustomStatus: (String) -> Unit = {},
    onNavigateBack: () -> Unit,
    currentTab: BottomNavTab,
    onTabSelected: (BottomNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedStatus by remember(currentStatus) { mutableStateOf(currentStatus) }
    var customStatusInput by remember { mutableStateOf("") }

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
                                Icon(
                                    imageVector = Icons.Default.Work,
                                    contentDescription = "Status",
                                    tint = DesignTokens.Colors.PrimaryPink,
                                    modifier = Modifier.size(36.dp)
                                )
                            }

                            Text(
                                text = "Currently $selectedStatus",
                                color = DesignTokens.Colors.PrimaryPink,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "Updated $lastUpdated",
                                color = DesignTokens.Colors.TextSecondary,
                                fontSize = 13.sp
                            )
                        }
                    }

                    // Quick Select Section
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
                                    val isSelected = selectedStatus.equals(item.label, ignoreCase = true)
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

                    // Custom Status Card
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
                                            onAddCustomStatus(text)
                                            customStatusInput = ""
                                        }
                                    }
                                )
                            }
                        }
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
