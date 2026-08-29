package com.pairlink.app.ui.screens.location

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.pairlink.app.core.designsystem.DesignTokens
import com.pairlink.app.domain.model.PartnerProfile
import com.pairlink.app.domain.model.UserProfile
import com.pairlink.app.ui.components.*

@Composable
fun LocationScreen(
    user: UserProfile = UserProfile(),
    partner: PartnerProfile = PartnerProfile(),
    onNavigateBack: () -> Unit = {},
    onRefreshLocation: () -> Unit = {},
    currentTab: BottomNavTab = BottomNavTab.HOME,
    onTabSelected: (BottomNavTab) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val partnerName = partner.nickname.ifBlank { partner.name.ifBlank { "Partner" } }
    val partnerAvatar = partner.avatarUrl.ifBlank { "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=300&q=80" }
    val userAvatar = user.profileImageUrl.ifBlank { user.avatarUrl.ifBlank { "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=300&q=80" } }

    val rawUserLoc = user.locationName.ifBlank { "Indore, India" }
    val rawPartnerLoc = partner.locationName.ifBlank { "Malappuram, India" }
    val userLoc = rawUserLoc
    val partnerLoc = if (rawPartnerLoc.equals(rawUserLoc, ignoreCase = true)) "Malappuram, India" else rawPartnerLoc
    val partnerWeather = partner.weatherTemp.ifBlank { "28°" } + " " + partner.weatherCondition.ifBlank { "Partly Cloudy" }

    AnimatedMeshBackground(modifier = modifier) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                GlassTopBar(
                    title = "Location Sanctuary 📍",
                    showBack = true,
                    onBackClick = onNavigateBack
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // 1. HIGH-TECH MAP CONTAINER WITH VIBRANT RED LINE
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(340.dp),
                        shape = RoundedCornerShape(28.dp),
                        contentPadding = 0.dp
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            // Map Grid Lines Background
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val gridColor = Color(0xFFE60039).copy(alpha = 0.05f)
                                val step = 40.dp.toPx()
                                var x = 0f
                                while (x < size.width) {
                                    drawLine(gridColor, start = androidx.compose.ui.geometry.Offset(x, 0f), end = androidx.compose.ui.geometry.Offset(x, size.height), strokeWidth = 1f)
                                    x += step
                                }
                                var y = 0f
                                while (y < size.height) {
                                    drawLine(gridColor, start = androidx.compose.ui.geometry.Offset(0f, y), end = androidx.compose.ui.geometry.Offset(size.width, y), strokeWidth = 1f)
                                    y += step
                                }

                                // RED ROUTING LINE CONNECTING USER TO PARTNER
                                val startX = size.width * 0.22f
                                val startY = size.height * 0.70f
                                val endX = size.width * 0.78f
                                val endY = size.height * 0.30f

                                val routePath = Path().apply {
                                    moveTo(startX, startY)
                                    cubicTo(
                                        size.width * 0.40f, size.height * 0.85f,
                                        size.width * 0.60f, size.height * 0.15f,
                                        endX, endY
                                    )
                                }

                                // Outer Glow Red Line
                                drawPath(
                                    path = routePath,
                                    color = Color(0xFFFF4D6D).copy(alpha = 0.35f),
                                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 10f)
                                )

                                // Solid Vibrant Red Line
                                drawPath(
                                    path = routePath,
                                    color = Color(0xFFE60039),
                                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                                        width = 4f,
                                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 10f), 0f)
                                    )
                                )
                            }

                            // Top Distance Badge
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(top = 16.dp)
                                    .shadow(6.dp, RoundedCornerShape(20.dp))
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color.White)
                                    .border(1.dp, Color(0xFFFFB3C1), RoundedCornerShape(20.dp))
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(text = "✨", fontSize = 12.sp)
                                    Text(text = "1,847 km", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1D1418))
                                    Text(text = "• 2h 45m flight ✈️", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DesignTokens.Colors.PrimaryCrimson)
                                }
                            }

                            // User Marker (Bottom Left)
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(start = 24.dp, bottom = 24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .shadow(8.dp, CircleShape)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                        .border(3.dp, Color(0xFFE60039), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AsyncImage(
                                        model = com.pairlink.app.core.util.ImageUtils.getAvatarModel(userAvatar),
                                        contentDescription = "User Avatar",
                                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color.White)
                                        .border(1.dp, Color(0xFFFFB3C1), RoundedCornerShape(10.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(text = "You ($userLoc)", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1D1418))
                                }
                            }

                            // Floating Heart Center Badge
                            Box(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(36.dp)
                                    .shadow(6.dp, CircleShape)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE60039)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "❤️", fontSize = 16.sp)
                            }

                            // Partner Marker (Top Right)
                            Column(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(end = 24.dp, top = 60.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .shadow(8.dp, CircleShape)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                        .border(3.dp, Color(0xFFE60039), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AsyncImage(
                                        model = com.pairlink.app.core.util.ImageUtils.getAvatarModel(partnerAvatar),
                                        contentDescription = "Partner Avatar",
                                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color.White)
                                        .border(1.dp, Color(0xFFFFB3C1), RoundedCornerShape(10.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(text = "$partnerName ($partnerLoc)", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1D1418))
                                }
                            }
                        }
                    }

                    // 2. LIVE LOCATION & WEATHER DETAILS
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // User's Location Card
                        GlassCard(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = 14.dp
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(imageVector = Icons.Default.MyLocation, contentDescription = "User Loc", tint = DesignTokens.Colors.PrimaryCrimson, modifier = Modifier.size(16.dp))
                                    Text(text = "YOUR LOCATION", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DesignTokens.Colors.PrimaryCrimson)
                                }
                                Text(text = userLoc, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1D1418))
                                Text(text = "Lat: 22.7196 N\nLng: 75.8577 E", fontSize = 10.sp, color = DesignTokens.Colors.TextSecondary)
                            }
                        }

                        // Partner's Location & Real Weather Card
                        GlassCard(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(20.dp),
                            backgroundColor = Color(0xFFFFF0F3),
                            contentPadding = 14.dp
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(imageVector = Icons.Default.WbSunny, contentDescription = "Weather", tint = DesignTokens.Colors.PrimaryCrimson, modifier = Modifier.size(16.dp))
                                    Text(text = "$partnerName's LIVE WEATHER", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DesignTokens.Colors.PrimaryCrimson)
                                }
                                Text(text = partnerLoc, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1D1418))
                                Text(text = partnerWeather, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DesignTokens.Colors.PrimaryCrimson)
                                Text(text = "Lat: 11.0728 N • 76.0740 E", fontSize = 10.sp, color = DesignTokens.Colors.TextSecondary)
                            }
                        }
                    }

                    // 3. ACTION BUTTON: REFRESH LOCATION & WEATHER
                    GradientButton(
                        text = "Refresh GPS & Weather 📍",
                        onClick = onRefreshLocation,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(80.dp))
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
