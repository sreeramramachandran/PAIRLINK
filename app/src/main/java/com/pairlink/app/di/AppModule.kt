package com.pairlink.app.di

import android.content.Context
import androidx.room.Room
import com.pairlink.app.data.local.PairLinkDatabase
import com.pairlink.app.data.local.dao.NotificationDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApplicationContext(@ApplicationContext context: Context): Context = context

    @Provides
    @Singleton
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Singleton
    fun providePairLinkDatabase(@ApplicationContext context: Context): PairLinkDatabase {
        return Room.databaseBuilder(
            context,
            PairLinkDatabase::class.java,
            "pairlink.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideNotificationDao(database: PairLinkDatabase): NotificationDao {
        return database.notificationDao()
    }
}
