package com.example.goplanify.di

import com.example.goplanify.domain.repository.AuthenticationRepository
import com.example.goplanify.domain.repository.ItineraryRepository
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
    fun provideAuthenticationRepository(): AuthenticationRepository {
        return AuthenticationRepository()
    }

}