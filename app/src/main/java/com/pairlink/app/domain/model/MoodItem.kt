package com.pairlink.app.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Mood option data model with emoji, icon, float animation offset, and custom deletion indicator.
 */
data class MoodItem(
    val id: String,
    val label: String,
    val emoji: String,
    val icon: ImageVector,
    val floatDelayMs: Int = 0,
    val isCustom: Boolean = false
)

val DefaultMoodOptions = listOf(
    MoodItem("happy", "Happy", "😊", Icons.Default.SentimentSatisfied, 0, isCustom = false),
    MoodItem("missing_you", "Missing You", "🥺", Icons.Default.Favorite, 300, isCustom = false),
    MoodItem("sleeping", "Sleeping", "😴", Icons.Default.Bedtime, 600, isCustom = false),
    MoodItem("busy", "Busy", "💻", Icons.Default.Work, 200, isCustom = false),
    MoodItem("driving", "Driving", "🚗", Icons.Default.DirectionsCar, 500, isCustom = false),
    MoodItem("sad", "Sad", "😢", Icons.Default.SentimentDissatisfied, 400, isCustom = false),
    MoodItem("thinking_of_you", "Thinking of You", "❤️", Icons.Default.Psychology, 100, isCustom = false),
    MoodItem("need_a_hug", "Need a Hug", "🤗", Icons.Default.VolunteerActivism, 700, isCustom = false),
    MoodItem("excited", "Excited", "🎉", Icons.Default.Celebration, 350, isCustom = false)
)
