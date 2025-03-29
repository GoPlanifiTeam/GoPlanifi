package com.example.goplanify.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.goplanify.data.local.converters.DateConverter
import com.example.goplanify.data.local.dao.AuthenticationDao
import com.example.goplanify.data.local.dao.ItineraryDao
import com.example.goplanify.data.local.dao.PreferencesDao
import com.example.goplanify.data.local.dao.TripDao
import com.example.goplanify.data.local.dao.UserDao
import com.example.goplanify.data.local.entity.ItineraryItemEntity
import com.example.goplanify.data.local.entity.TripEntity
import com.example.goplanify.data.local.entity.UserEntity
import com.example.goplanify.data.local.entity.AuthenticationEntity
import com.example.goplanify.data.local.entity.PreferencesEntity

@Database(
    entities = [ItineraryItemEntity::class, TripEntity::class,  UserEntity::class, AuthenticationEntity::class, PreferencesEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class) //Para tener un dateTime
abstract class AppDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
    abstract fun itineraryDao(): ItineraryDao
    abstract fun userDao(): UserDao
    abstract fun authenticationDao(): AuthenticationDao
    abstract fun preferencesDao(): PreferencesDao
}