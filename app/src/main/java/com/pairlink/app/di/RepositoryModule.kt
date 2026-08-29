package com.pairlink.app.di

import com.pairlink.app.data.repository.AuthRepository
import com.pairlink.app.data.repository.FirebaseAuthRepository
import com.pairlink.app.data.repository.FirebaseMoodRepository
import com.pairlink.app.data.repository.FirebasePairRepository
import com.pairlink.app.data.repository.FirebasePartnerRepository
import com.pairlink.app.data.repository.FirebasePresenceRepository
import com.pairlink.app.data.repository.FirebaseStatusRepository
import com.pairlink.app.data.repository.MoodRepository
import com.pairlink.app.data.repository.NotificationRepository
import com.pairlink.app.data.repository.PairRepository
import com.pairlink.app.data.repository.PartnerRepository
import com.pairlink.app.data.repository.PresenceRepository
import com.pairlink.app.data.repository.RoomNotificationRepository
import com.pairlink.app.data.repository.StatusRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        firebaseAuthRepository: FirebaseAuthRepository
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindPairRepository(
        firebasePairRepository: FirebasePairRepository
    ): PairRepository

    @Binds
    @Singleton
    abstract fun bindPartnerRepository(
        firebasePartnerRepository: FirebasePartnerRepository
    ): PartnerRepository

    @Binds
    @Singleton
    abstract fun bindPresenceRepository(
        firebasePresenceRepository: FirebasePresenceRepository
    ): PresenceRepository

    @Binds
    @Singleton
    abstract fun bindMoodRepository(
        firebaseMoodRepository: FirebaseMoodRepository
    ): MoodRepository

    @Binds
    @Singleton
    abstract fun bindStatusRepository(
        firebaseStatusRepository: FirebaseStatusRepository
    ): StatusRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(
        roomNotificationRepository: RoomNotificationRepository
    ): NotificationRepository

    @Binds
    @Singleton
    abstract fun bindWeatherRepository(
        openMeteoWeatherRepository: com.pairlink.app.data.repository.OpenMeteoWeatherRepository
    ): com.pairlink.app.data.repository.WeatherRepository
}
