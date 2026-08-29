package com.pairlink.app.ui.screens.home

import android.app.Activity
import android.app.DatePickerDialog
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.pairlink.app.core.designsystem.DesignTokens
import com.pairlink.app.domain.model.PartnerProfile
import com.pairlink.app.domain.model.PresenceRingState
import com.pairlink.app.domain.model.UserProfile
import com.pairlink.app.ui.components.*
import java.time.LocalDate

@Composable
fun HomeScreen(
    partner: PartnerProfile,
    user: UserProfile = UserProfile(),
    partnerDisplayName: String = "",
    togetherTime: String,
    birthdayDaysLeft: String,
    nextMeetingDaysLeft: String = "27",
    nextMeetingHoursLeft: String = "14",
    nextMeetingMinsLeft: String = "32",
    nextMeetingDateText: String = "24 Dec 2026",
    nextMeetingRawDate: String = "2026-12-24",
    heartbeatCount: Int,
    isPartnerHolding: Boolean = false,
    presenceRingState: PresenceRingState = PresenceRingState.ONLINE,
    onHeartPressed: () -> Unit = {},
    onHeartReleased: () -> Unit = {},
    onSendHeart: () -> Unit = {},
    onSaveNextMeetingDate: (String) -> Unit = {},
    onSaveRelationshipDate: (String) -> Unit = {},
    onSavePartnerBirthday: (String) -> Unit = {},
    onNavigateToBirthday: () -> Unit = {},
    onNavigateToRelationship: () -> Unit = {},
    onNavigateToLocation: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    currentTab: BottomNavTab = BottomNavTab.HOME,
    onTabSelected: (BottomNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    BackHandler(enabled = true) {
        (context as? Activity)?.moveTaskToBack(true)
    }

    val displayName = partnerDisplayName.ifBlank { partner.name.ifBlank { "Partner" } }
    val partnerAvatar = partner.avatarUrl.ifBlank { "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=300&q=80" }
    val userAvatar = user.profileImageUrl.ifBlank { user.avatarUrl.ifBlank { "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=300&q=80" } }

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

    // Date Picker Dialog for Editable Relationship Date
    val initialRelDate = try {
        if (user.relationshipStartDate.isNotBlank()) LocalDate.parse(user.relationshipStartDate) else LocalDate.now()
    } catch (_: Exception) {
        LocalDate.now()
    }
    val relationshipDatePicker = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selected = LocalDate.of(year, month + 1, dayOfMonth)
                onSaveRelationshipDate(selected.toString())
            },
            initialRelDate.year,
            initialRelDate.monthValue - 1,
            initialRelDate.dayOfMonth
        )
    }

    // Date Picker Dialog for Editable Partner Birthday
    val initialBday = try {
        if (partner.dob.isNotBlank()) LocalDate.parse(partner.dob) else LocalDate.of(2000, 1, 1)
    } catch (_: Exception) {
        LocalDate.of(2000, 1, 1)
    }
    val partnerBirthdayPicker = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selected = LocalDate.of(year, month + 1, dayOfMonth)
                onSavePartnerBirthday(selected.toString())
            },
            initialBday.year,
            initialBday.monthValue - 1,
            initialBday.dayOfMonth
        )
    }

    AnimatedMeshBackground(modifier = modifier) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top App Bar
                GlassTopBar(
                    title = "Sreeram & $displayName ❤️",
                    partnerAvatarUrl = partnerAvatar,
                    userAvatarUrl = userAvatar,
                    onAvatarClick = onNavigateToProfile,
                    onNotificationClick = { onTabSelected(BottomNavTab.UPDATES) },
                    onCalendarClick = onNavigateToBirthday,
                    onSendHeartClick = onSendHeart
                )

                // Scrollable Sanctuary Body
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // 1. DISTANCE BANNER CARD ("The Distance Between Us")
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        contentPadding = 16.dp
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Location Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "📍", fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "Indore, India", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = DesignTokens.Colors.TextSecondary)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "Malappuram, India", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = DesignTokens.Colors.TextSecondary)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "📍", fontSize = 12.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Distance Title & Big Numbers
                            Text(text = "THE DISTANCE BETWEEN US", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DesignTokens.Colors.TextSecondary, letterSpacing = 1.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(text = "✨", fontSize = 14.sp)
                                Text(text = "1,847 km", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1D1418))
                                Text(text = "✨", fontSize = 14.sp)
                            }
                            Text(text = "But never far at heart ♡", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = DesignTokens.Colors.TextSecondary)

                            Spacer(modifier = Modifier.height(12.dp))

                            // Dual Avatars with Curved Dotted Path
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(70.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Canvas(modifier = Modifier.fillMaxWidth().height(40.dp)) {
                                    val path = Path().apply {
                                        moveTo(size.width * 0.20f, size.height * 0.50f)
                                        quadraticTo(size.width * 0.50f, size.height * 0.90f, size.width * 0.80f, size.height * 0.50f)
                                    }
                                    drawPath(
                                        path = path,
                                        color = Color(0xFFFF809B),
                                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                                            width = 3f,
                                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                                        )
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .shadow(4.dp, CircleShape)
                                        .clip(CircleShape)
                                        .background(Color.White),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "❤️", fontSize = 12.sp)
                                }

                                AsyncImage(
                                    model = com.pairlink.app.core.util.ImageUtils.getAvatarModel(userAvatar),
                                    contentDescription = "User Avatar",
                                    modifier = Modifier
                                        .align(Alignment.CenterStart)
                                        .padding(start = 16.dp)
                                        .size(56.dp)
                                        .clip(CircleShape)
                                        .border(2.dp, Color(0xFFFF809B), CircleShape),
                                    contentScale = ContentScale.Crop
                                )

                                AsyncImage(
                                    model = com.pairlink.app.core.util.ImageUtils.getAvatarModel(partnerAvatar),
                                    contentDescription = "Partner Avatar",
                                    modifier = Modifier
                                        .align(Alignment.CenterEnd)
                                        .padding(end = 16.dp)
                                        .size(56.dp)
                                        .clip(CircleShape)
                                        .border(2.dp, Color(0xFFFF809B), CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            GradientButton(
                                text = "Send Love",
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Favorite,
                                        contentDescription = "Send Love",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                onClick = {
                                    onHeartPressed()
                                    onSendHeart()
                                },
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                        }
                    }

                    // 2. EDITABLE & SYNCED "NEXT TIME TOGETHER" COUNTDOWN CARD
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        backgroundColor = Color(0xFFFFF0F3),
                        contentPadding = 14.dp,
                        onClick = { nextMeetingDatePicker.show() }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(text = "✈️ NEXT TIME TOGETHER", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DesignTokens.Colors.PrimaryCrimson)
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit Meeting Date",
                                        tint = DesignTokens.Colors.PrimaryCrimson,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(text = nextMeetingDaysLeft, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1D1418))
                                        Text(text = "DAYS", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = DesignTokens.Colors.TextSecondary)
                                    }
                                    Text(text = ":", fontSize = 16.sp, color = DesignTokens.Colors.TextSecondary)
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(text = nextMeetingHoursLeft, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1D1418))
                                        Text(text = "HOURS", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = DesignTokens.Colors.TextSecondary)
                                    }
                                    Text(text = ":", fontSize = 16.sp, color = DesignTokens.Colors.TextSecondary)
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(text = nextMeetingMinsLeft, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1D1418))
                                        Text(text = "MINS", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = DesignTokens.Colors.TextSecondary)
                                    }
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = "Our next meeting ❤️", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = DesignTokens.Colors.TextSecondary)
                                Text(text = nextMeetingDateText, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = DesignTokens.Colors.PrimaryCrimson)
                                Text(text = "Tap to change date ✏️", fontSize = 9.sp, fontWeight = FontWeight.Medium, color = DesignTokens.Colors.TextSecondary)
                            }
                        }
                    }

                    // 3. RELATIONSHIP DAYS COUNTER & PARTNER BIRTHDAY CARDS
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Together For Relationship Card (Locked)
                        RelationshipCard(
                            modifier = Modifier.weight(1f),
                            togetherTime = togetherTime,
                            title = "Together For",
                            onClick = null
                        )

                        // Partner Birthday Card (Locked)
                        BirthdayCard(
                            modifier = Modifier.weight(1f),
                            daysLeft = birthdayDaysLeft,
                            title = "$displayName's Birthday",
                            onClick = null
                        )
                    }

                    // 4. 4 QUICK ACTION GRID CARDS
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuickActionItem(modifier = Modifier.weight(1f), icon = Icons.Default.ChatBubble, title = "Chat", sub = "Talk anytime", onClick = { onTabSelected(BottomNavTab.UPDATES) })
                        QuickActionItem(modifier = Modifier.weight(1f), icon = Icons.Default.PhotoLibrary, title = "Memories", sub = "Together for", onClick = onNavigateToRelationship)
                        QuickActionItem(modifier = Modifier.weight(1f), icon = Icons.Default.CardGiftcard, title = "Surprise", sub = "Birthday", onClick = onNavigateToBirthday)
                        QuickActionItem(modifier = Modifier.weight(1f), icon = Icons.Default.LocationOn, title = "Location", sub = "Map view", onClick = onNavigateToLocation)
                    }

                    // 5. TODAY'S CONNECTION CARD
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        contentPadding = 14.dp
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(Color(0xFFFFF0F3))
                                            .border(1.dp, Color(0xFFFFB3C1), RoundedCornerShape(14.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(imageVector = Icons.Default.Favorite, contentDescription = "Heart", tint = DesignTokens.Colors.PrimaryCrimson, modifier = Modifier.size(24.dp))
                                    }

                                    Column {
                                        Text(text = "TODAY'S CONNECTION 💬", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DesignTokens.Colors.PrimaryCrimson)
                                        Text(text = "What is one thing you wish we could do together today?", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D1418), maxLines = 2, overflow = TextOverflow.Ellipsis)
                                    }
                                }

                                GradientButton(
                                    text = "Answer >",
                                    onClick = { onTabSelected(BottomNavTab.MOOD) },
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFF0F3))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "Both of you haven't answered yet 🔒", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = DesignTokens.Colors.TextSecondary)
                                    Icon(imageVector = Icons.Default.Lock, contentDescription = "Locked", tint = DesignTokens.Colors.TextSecondary, modifier = Modifier.size(12.dp))
                                }
                            }
                        }
                    }

                    // 6. NOTE CARD & WEATHER GRID
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Left Note Card
                        GlassCard(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(20.dp),
                            backgroundColor = Color(0xFFFFF0F3),
                            contentPadding = 12.dp
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(text = "$displayName sent a note 💌", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DesignTokens.Colors.PrimaryCrimson)
                                Text(text = "\"Thinking of you a little extra today... miss you always! ❤️\"", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1D1418))
                                Spacer(modifier = Modifier.height(4.dp))
                                GradientButton(
                                    text = "Read Now",
                                    onClick = { onTabSelected(BottomNavTab.UPDATES) },
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }

                        // Right Weather Card
                        GlassCard(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = 12.dp
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(text = "📍 Malappuram", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DesignTokens.Colors.TextSecondary)
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                    Text(text = "28°", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1D1418))
                                    Text(text = "🌤️", fontSize = 20.sp)
                                }
                                Text(text = "Partly Cloudy", fontSize = 10.sp, color = DesignTokens.Colors.TextSecondary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFFFF0F3))
                                        .padding(6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "9:41 PM ❤️ 9:41 PM", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DesignTokens.Colors.PrimaryCrimson)
                                }
                            }
                        }
                    }

                    // Bottom padding for nav bar
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }

            // Bottom Nav Bar
            GlassBottomNavigation(
                currentTab = currentTab,
                onTabSelected = onTabSelected,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
private fun QuickActionItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    sub: String,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        contentPadding = 8.dp,
        onClick = onClick
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFFF0F3))
                    .border(1.dp, Color(0xFFFFB3C1), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = title, tint = DesignTokens.Colors.PrimaryCrimson, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D1418), maxLines = 1)
            Text(text = sub, fontSize = 8.sp, color = DesignTokens.Colors.TextSecondary, maxLines = 1)
        }
    }
}
