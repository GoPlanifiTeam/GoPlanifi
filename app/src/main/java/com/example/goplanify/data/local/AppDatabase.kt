package com.example.goplanify.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.goplanify.data.local.converters.DateConverter
import com.example.goplanify.data.local.dao.ItineraryItemDao
import com.example.goplanify.data.local.dao.TripDao
import com.example.goplanify.data.local.entity.ItineraryItemEntity
import com.example.goplanify.data.local.entity.TripEntity

@Database(
    entities = [
        TripEntity::class,
        ItineraryItemEntity::class,
    ],
    version = 1,
    exportSchema = false
)

abstract class AppDatabase : RoomDatabase() {
    abstract fun itineraryItemDao(): ItineraryItemDao;
    abstract fun tripDao(): TripDao;
}