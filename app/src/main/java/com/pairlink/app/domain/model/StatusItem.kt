package com.pairlink.app.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Status option data model for quick activity status selection with custom status deletion support.
 */
data class StatusItem(
    val id: String,
    val label: String,
    val icon: ImageVector,
    val emoji: String = "",
    val isCustom: Boolean = false
)

val DefaultStatusOptions = listOf(
    StatusItem("reached_home", "Reached Home", Icons.Default.Home, "🏠", isCustom = false),
    StatusItem("working", "Working", Icons.Default.Work, "💻", isCustom = false),
    StatusItem("sleeping", "Sleeping", Icons.Default.Bed, "😴", isCustom = false),
    StatusItem("driving", "Driving", Icons.Default.DirectionsCar, "🚗", isCustom = false),
    StatusItem("eating", "Eating", Icons.Default.Restaurant, "🍽", isCustom = false),
    StatusItem("shopping", "Shopping", Icons.Default.ShoppingBag, "🛒", isCustom = false),
    StatusItem("travelling", "Travelling", Icons.Default.Flight, "✈", isCustom = false),
    StatusItem("exercising", "Exercising", Icons.AutoMirrored.Filled.DirectionsRun, "🏃", isCustom = false),
    StatusItem("studying", "Studying", Icons.AutoMirrored.Filled.MenuBook, "📚", isCustom = false)
)
