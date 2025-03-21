package com.example.goplanify.di

import com.example.goplanify.domain.repository.AIRecommendationsRepository
import com.example.goplanify.domain.repository.AuthenticationRepository
import com.example.goplanify.domain.repository.ImageRepository
import com.example.goplanify.domain.repository.ItineraryRepository
import com.example.goplanify.domain.repository.MapRepository
import com.example.goplanify.domain.repository.PreferencesRepository
import com.example.goplanify.domain.repository.TripRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTripRepository(): TripRepository {
        return TripRepository()
    }

    @Provides
    @Singleton
    fun provideItineraryRepository(): ItineraryRepository {
        return ItineraryRepository()
    }

    @Provides
    @Singleton
    fun providePreferencesRepository(): PreferencesRepository {
        return PreferencesRepository()
    }

    @Provides
    @Singleton
    fun provideImageRepository(): ImageRepository {
        return ImageRepository()
    }

    @Provides
    @Singleton
    fun provideMapRepository(): MapRepository {
        return MapRepository()
    }

    @Provides
    @Singleton
    fun provideAuthenticationRepository(): AuthenticationRepository {
        return AuthenticationRepository()
    }

    @Provides
    @Singleton
    fun provideAIRecommendationsRepository(): AIRecommendationsRepository {
        return AIRecommendationsRepository()
    }
}