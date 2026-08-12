package com.pairlink.app.domain.model

import androidx.compose.ui.graphics.vector.ImageVector

enum class NotificationColorType {
    PRIMARY,
    SECONDARY,
    TERTIARY
}

data class UpdateNotification(
    val id: String,
    val title: String,
    val time: String,
    val description: String,
    val icon: ImageVector,
    val colorType: NotificationColorType = NotificationColorType.PRIMARY
)
