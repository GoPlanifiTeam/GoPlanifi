package com.example.goplanify.di

import android.content.Context
import androidx.room.Room
import com.example.goplanify.data.local.AppDatabase
import com.example.goplanify.data.local.converters.DateConverter
import com.example.goplanify.data.local.dao.*
import com.example.goplanify.data.repository.*
import com.example.goplanify.domain.repository.*
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // Room Database
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "goplanify_db"
        ).fallbackToDestructiveMigration()
            .build()
    }

    // Gson
    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    // DAOs
    @Provides fun provideTripDao(db: AppDatabase): TripDao = db.tripDao()
    @Provides fun provideItineraryDao(db: AppDatabase): ItineraryDao = db.itineraryDao()
    @Provides fun provideUserDao(db: AppDatabase): UserDao = db.userDao()
    @Provides fun provideAuthenticationDao(db: AppDatabase): AuthenticationDao = db.authenticationDao()
    @Provides fun providePreferencesDao(db: AppDatabase): PreferencesDao = db.preferencesDao()

    // Repositories (con interfaz)
    @Provides
    @Singleton
    fun provideTripRepository(
        tripDao: TripDao,
        userDao: UserDao,
        itineraryDao: ItineraryDao
    ): TripRepository =
        TripRepositoryImpl(tripDao, userDao, itineraryDao)

    @Provides
    @Singleton
    fun provideItineraryRepository(itineraryDao: ItineraryDao): ItineraryRepository =
        ItineraryRepositoryImpl(itineraryDao)

    @Provides
    @Singleton
    fun provideUserRepository(userDao: UserDao, tripDao: TripDao): UserRepository =
        UserRepositoryImpl(userDao,tripDao)

    @Provides
    @Singleton
    fun provideAuthenticationRepository(authDao: AuthenticationDao): AuthenticationRepository =
        AuthenticationRepositoryImpl(authDao)

    @Provides
    @Singleton
    fun providePreferencesRepository(prefsDao: PreferencesDao): PreferencesRepository =
        PreferencesRepositoryImpl(prefsDao)
}
