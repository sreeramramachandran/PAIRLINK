package com.pairlink.app.ui.screens.mood

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pairlink.app.core.designsystem.DesignTokens
import com.pairlink.app.domain.model.MoodItem
import com.pairlink.app.ui.components.AnimatedMeshBackground
import com.pairlink.app.ui.components.BottomNavTab
import com.pairlink.app.ui.components.GlassBottomNavigation
import com.pairlink.app.ui.components.GlassTextField
import com.pairlink.app.ui.components.GlassTopBar
import com.pairlink.app.ui.components.GradientButton
import com.pairlink.app.ui.components.MoodChip

/**
 * Mood Screen allowing user to select, create, or delete custom moods.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MoodScreen(
    currentMood: String,
    availableMoods: List<MoodItem>,
    onSaveMood: (String) -> Unit,
    onDeleteMood: (String) -> Unit = {},
    onNavigateBack: () -> Unit,
    currentTab: BottomNavTab,
    onTabSelected: (BottomNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedMood by remember(currentMood) { mutableStateOf(currentMood) }
    var customMoodText by remember { mutableStateOf("") }

    AnimatedMeshBackground(modifier = modifier) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                GlassTopBar(
                    title = "Mood",
                    showBack = true,
                    onBackClick = onNavigateBack
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Header Question
                    Text(
                        text = "How are you feeling?",
                        color = DesignTokens.Colors.TextPrimary,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    // Floating Mood Chips
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
                            text = "CUSTOM MOOD",
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

                    // Save Button
                    GradientButton(
                        text = "Save Mood",
                        onClick = {
                            val finalMood = customMoodText.trim().ifBlank { selectedMood }
                            onSaveMood(finalMood)
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
