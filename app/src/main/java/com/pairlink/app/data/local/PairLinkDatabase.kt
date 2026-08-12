package com.pairlink.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pairlink.app.data.local.dao.NotificationDao
import com.pairlink.app.data.local.entity.NotificationEntity

/**
 * PairLink Room Database.
 */
@Database(
    entities = [NotificationEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PairLinkDatabase : RoomDatabase() {
    abstract fun notificationDao(): NotificationDao
}
