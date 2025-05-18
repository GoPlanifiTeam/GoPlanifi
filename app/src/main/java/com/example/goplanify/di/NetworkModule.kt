package com.example.goplanify.di

import com.example.goplanify.data.remote.api.HotelApiService
import com.example.goplanify.data.remote.api.RetrofitClient
import com.example.goplanify.data.repository.HotelRepositoryImpl
import com.example.goplanify.domain.repository.HotelRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideHotelApiService(): HotelApiService {
        return RetrofitClient.hotelApiService
    }

}
