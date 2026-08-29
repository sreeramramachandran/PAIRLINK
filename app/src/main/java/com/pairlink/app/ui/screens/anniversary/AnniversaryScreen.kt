package com.pairlink.app.ui.screens.anniversary

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.pairlink.app.ui.components.*
import java.time.LocalDate

/**
 * Anniversary & Milestones Screen celebrating shared journey duration & Next Meeting countdown.
 */
@Composable
fun AnniversaryScreen(
    togetherTime: String,
    startDate: String,
    nextMeetingDaysLeft: String = "27",
    nextMeetingHoursLeft: String = "14",
    nextMeetingMinsLeft: String = "32",
    nextMeetingDateText: String = "24 Dec 2026",
    nextMeetingRawDate: String = "2026-12-24",
    onSaveNextMeetingDate: (String) -> Unit = {},
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

    // Date Picker Dialog for Editable "Next Time Together"
    val initialNextDate = try {
        if (nextMeetingRawDate.isNotBlank()) LocalDate.parse(nextMeetingRawDate) else LocalDate.now().plusDays(27)
    } catch (_: Exception) {
        LocalDate.now().plusDays(27)
    }
    val nextMeetingDatePicker = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selected = LocalDate.of(year, month + 1, dayOfMonth)
                onSaveNextMeetingDate(selected.toString())
            },
            initialNextDate.year,
            initialNextDate.monthValue - 1,
            initialNextDate.dayOfMonth
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
                GlassTopBar(
                    title = "Memories & Milestones",
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
                    // 1. Hero Together Card
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(DesignTokens.Radius.SuperLarge),
                        contentPadding = 24.dp
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .shadow(16.dp, CircleShape, spotColor = Color(0xFFE60039))
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFF0F3))
                                    .border(1.5.dp, Color(0xFFFFB3C1), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "Together",
                                    tint = DesignTokens.Colors.PrimaryCrimson,
                                    modifier = Modifier.size(32.dp)
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
                                color = DesignTokens.Colors.PrimaryCrimson,
                                fontSize = 38.sp,
                                fontWeight = FontWeight.ExtraBold
                            )

                            if (isDateLocked) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50.dp))
                                        .background(Color(0xFFFFF0F3))
                                        .border(1.dp, Color(0xFFFFB3C1), RoundedCornerShape(50.dp))
                                        .padding(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Locked Date",
                                        tint = DesignTokens.Colors.PrimaryCrimson,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "Since $startDate (Locked)",
                                        color = Color(0xFF1D1418),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            } else {
                                GlassButton(
                                    text = "Set Anniversary Date",
                                    onClick = { datePickerDialog.show() },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.CalendarMonth,
                                            contentDescription = "Set Date",
                                            tint = DesignTokens.Colors.PrimaryCrimson,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    },
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }

                    // 2. NEXT MEETING COUNTDOWN SECTION
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        backgroundColor = Color(0xFFFFF0F3),
                        contentPadding = 16.dp,
                        onClick = { nextMeetingDatePicker.show() }
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(text = "✈️ NEXT TIME TOGETHER", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DesignTokens.Colors.PrimaryCrimson)
                                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Date", tint = DesignTokens.Colors.PrimaryCrimson, modifier = Modifier.size(14.dp))
                                }
                                Text(text = "Target: $nextMeetingDateText ✏️", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D1418))
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = nextMeetingDaysLeft, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1D1418))
                                    Text(text = "DAYS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = DesignTokens.Colors.TextSecondary)
                                }
                                Text(text = ":", fontSize = 20.sp, color = DesignTokens.Colors.TextSecondary)
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = nextMeetingHoursLeft, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1D1418))
                                    Text(text = "HOURS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = DesignTokens.Colors.TextSecondary)
                                }
                                Text(text = ":", fontSize = 20.sp, color = DesignTokens.Colors.TextSecondary)
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = nextMeetingMinsLeft, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1D1418))
                                    Text(text = "MINS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = DesignTokens.Colors.TextSecondary)
                                }
                            }
                        }
                    }

                    // 3. Milestones Section
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
                                            .background(Color(0xFFFFF0F3))
                                            .border(1.dp, Color(0xFFFFB3C1), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.WorkspacePremium,
                                            contentDescription = "Milestone",
                                            tint = DesignTokens.Colors.PrimaryCrimson,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text(
                                            text = title,
                                            color = Color(0xFF1D1418),
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
