package com.pairlink.app.ui.screens.anniversary

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pairlink.app.core.designsystem.DesignTokens
import com.pairlink.app.ui.components.AnimatedMeshBackground
import com.pairlink.app.ui.components.BottomNavTab
import com.pairlink.app.ui.components.GlassBottomNavigation
import com.pairlink.app.ui.components.GlassButton
import com.pairlink.app.ui.components.GlassCard
import com.pairlink.app.ui.components.GlassTopBar
import java.time.LocalDate

/**
 * Anniversary & Milestones Screen celebrating shared journey duration.
 * Once the anniversary date is set, it is locked and permanently immutable.
 */
@Composable
fun AnniversaryScreen(
    togetherTime: String,
    startDate: String,
    onSaveStartDate: (String) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    currentTab: BottomNavTab = BottomNavTab.UPDATES,
    onTabSelected: (BottomNavTab) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isDateLocked = startDate.isNotBlank() && startDate != "Set Date" && startDate != "—"

    val initialDate = try {
        LocalDate.parse(startDate)
    } catch (_: Exception) {
        LocalDate.now().minusYears(1)
    }

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selected = LocalDate.of(year, month + 1, dayOfMonth)
                onSaveStartDate(selected.toString())
            },
            initialDate.year,
            initialDate.monthValue - 1,
            initialDate.dayOfMonth
        )
    }

    val milestones = listOf(
        Pair("1000 Days Together", "Celebrated our 1000 days of connection."),
        Pair("2 Year Anniversary", "Two amazing years of laughter and growth together."),
        Pair("1 Year Anniversary", "First milestone reached together."),
        Pair("First Day Connected", if (isDateLocked) "$startDate — Where our story began. ❤️" else "Set your anniversary to start celebrating milestones.")
    )

    AnimatedMeshBackground(modifier = modifier) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Glass Top Bar with Back Arrow directly returning to Home
                GlassTopBar(
                    title = "Anniversary",
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
                    // Hero Together Card
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(DesignTokens.Radius.SuperLarge),
                        contentPadding = 28.dp
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .shadow(20.dp, CircleShape, spotColor = DesignTokens.Colors.Lavender)
                                    .clip(CircleShape)
                                    .background(DesignTokens.Colors.Lavender.copy(alpha = 0.25f))
                                    .border(1.5.dp, Color.White.copy(alpha = 0.4f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "Together",
                                    tint = DesignTokens.Colors.Lavender,
                                    modifier = Modifier.size(36.dp)
                                )
                            }

                            Text(
                                text = "TOGETHER FOR",
                                color = DesignTokens.Colors.TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp
                            )

                            Text(
                                text = togetherTime,
                                color = DesignTokens.Colors.PrimaryPink,
                                fontSize = 42.sp,
                                fontWeight = FontWeight.Bold
                            )

                            if (isDateLocked) {
                                // Locked Permanent Date Display
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50.dp))
                                        .background(Color.White.copy(alpha = 0.10f))
                                        .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(50.dp))
                                        .padding(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Locked Date",
                                        tint = DesignTokens.Colors.PrimaryPink,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "Since $startDate (Locked)",
                                        color = DesignTokens.Colors.TextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            } else {
                                // Initial Setup Option
                                Text(
                                    text = "No anniversary date set yet",
                                    color = DesignTokens.Colors.TextSecondary,
                                    fontSize = 14.sp
                                )

                                GlassButton(
                                    text = "Set Anniversary Date",
                                    onClick = { datePickerDialog.show() },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.CalendarMonth,
                                            contentDescription = "Set Date",
                                            tint = DesignTokens.Colors.PrimaryPink,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    },
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }
                    }

                    // Milestones Section
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "SHARED MILESTONES",
                            color = DesignTokens.Colors.TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        milestones.forEach { (title, description) ->
                            GlassCard(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(DesignTokens.Radius.Medium),
                                contentPadding = 16.dp
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(DesignTokens.Colors.SoftPink.copy(alpha = 0.20f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.WorkspacePremium,
                                            contentDescription = "Milestone",
                                            tint = DesignTokens.Colors.PrimaryPink,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text(
                                            text = title,
                                            color = DesignTokens.Colors.TextPrimary,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = description,
                                            color = DesignTokens.Colors.TextSecondary,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

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
