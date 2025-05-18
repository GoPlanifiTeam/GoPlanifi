package com.example.goplanify.di

import android.content.Context
import androidx.room.Room
import com.example.goplanify.BuildConfig
import com.example.goplanify.data.local.AppDatabase
import com.example.goplanify.data.local.converters.DateConverter
import com.example.goplanify.data.local.dao.*
import com.example.goplanify.data.remote.api.HotelApiService
import com.example.goplanify.data.repository.*
import com.example.goplanify.domain.repository.*
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
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
    @Provides fun provideItineraryImageDao(db: AppDatabase): ItineraryImageDao = db.itineraryImageDao() // Nuevo DAO
    @Provides fun provideReservationDao(database: AppDatabase): ReservationDao { return database.reservationDao() }
    // Repositories (con interfaz)
    @Provides
    @Singleton
    fun provideTripRepository(
        tripDao: TripDao,
        userDao: UserDao,
        itineraryDao: ItineraryDao,
        itineraryImageDao: ItineraryImageDao // Nuevo parámetro
    ): TripRepository =
        TripRepositoryImpl(tripDao, userDao, itineraryDao, itineraryImageDao)

    @Provides
    @Singleton
    fun provideItineraryRepository(
        itineraryDao: ItineraryDao,
        itineraryImageDao: ItineraryImageDao // Nuevo parámetro
    ): ItineraryRepository =
        ItineraryRepositoryImpl(itineraryDao, itineraryImageDao)

    @Provides
    @Singleton
    fun provideUserRepository(userDao: UserDao, tripDao: TripDao): UserRepository =
        UserRepositoryImpl(userDao, tripDao)

    @Provides
    @Singleton
    fun providePreferencesRepository(prefsDao: PreferencesDao): PreferencesRepository =
        PreferencesRepositoryImpl(prefsDao)

    // Retrofit
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // Retrofit
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.HOTELS_API_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    // Hotel Repository
    @Provides
    @Singleton
    fun provideHotelRepository(
        apiService: HotelApiService,
        reservationDao: ReservationDao
    ): HotelRepository {
        return HotelRepositoryImpl(apiService, reservationDao)
    }

}