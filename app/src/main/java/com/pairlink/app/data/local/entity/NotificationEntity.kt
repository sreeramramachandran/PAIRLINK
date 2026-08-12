package com.pairlink.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pairlink.app.domain.model.NotificationItem
import com.pairlink.app.domain.model.NotificationType

/**
 * Room Database Entity for local notification history.
 */
@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val message: String,
    val type: String,
    val timestamp: Long,
    val isRead: Boolean,
    val iconName: String,
    val senderUid: String
) {
    fun toDomain(): NotificationItem {
        val parsedType = try {
            NotificationType.valueOf(type)
        } catch (_: Exception) {
            NotificationType.SYSTEM
        }
        return NotificationItem(
            id = id,
            title = title,
            message = message,
            type = parsedType,
            timestamp = timestamp,
            isRead = isRead,
            iconName = iconName,
            senderUid = senderUid
        )
    }

    companion object {
        fun fromDomain(item: NotificationItem): NotificationEntity {
            return NotificationEntity(
                id = item.id,
                title = item.title,
                message = item.message,
                type = item.type.name,
                timestamp = item.timestamp,
                isRead = item.isRead,
                iconName = item.iconName,
                senderUid = item.senderUid
            )
        }
    }
}
