package com.pairlink.app.ui.screens.pairing

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pairlink.app.core.designsystem.DesignTokens
import com.pairlink.app.ui.components.AnimatedMeshBackground
import com.pairlink.app.ui.components.GlassButton
import com.pairlink.app.ui.components.GlassCard
import com.pairlink.app.ui.components.GlassTextField
import java.time.LocalDate

/**
 * Relationship Setup Screen to set anniversary / relationship start date with interactive DatePicker.
 */
@Composable
fun RelationshipSetupScreen(
    onSaveStartDate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var startDate by remember { mutableStateOf(LocalDate.now().toString()) }

    val initialDate = try {
        LocalDate.parse(startDate)
    } catch (_: Exception) {
        LocalDate.now()
    }

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selected = LocalDate.of(year, month + 1, dayOfMonth)
                startDate = selected.toString()
            },
            initialDate.year,
            initialDate.monthValue - 1,
            initialDate.dayOfMonth
        )
    }

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
                verticalArrangement = Arrangement.spacedBy(28.dp)
            ) {
                // Header
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Let's Personalize Your Journey ❤️",
                        color = DesignTokens.Colors.PrimaryPink,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        lineHeight = 34.sp
                    )
                    Text(
                        text = "Set your special date to unlock milestones.",
                        color = DesignTokens.Colors.TextSecondary,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center
                    )
                }

                // Date Picker Card
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(DesignTokens.Radius.ExtraLarge),
                    contentPadding = 24.dp
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "RELATIONSHIP START DATE",
                            color = DesignTokens.Colors.TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { datePickerDialog.show() },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            GlassTextField(
                                value = startDate,
                                onValueChange = { startDate = it },
                                placeholder = "YYYY-MM-DD",
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.CalendarMonth,
                                        contentDescription = "Date",
                                        tint = DesignTokens.Colors.PrimaryPink,
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clickable { datePickerDialog.show() }
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Text(
                            text = "This date is used to celebrate your anniversaries and track shared moments. You can change this later in settings.",
                            color = DesignTokens.Colors.TextSecondary.copy(alpha = 0.8f),
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // CTA Action Button
                GlassButton(
                    text = "Start Our Journey",
                    onClick = { onSaveStartDate(startDate) },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Start",
                            tint = DesignTokens.Colors.PrimaryPink,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )
            }
        }
    }
}
