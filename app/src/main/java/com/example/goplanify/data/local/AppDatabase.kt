package com.example.goplanify.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
    entities = [ItineraryItemEntity::class, TripEntity::class, UserEntity::class, AuthenticationEntity::class, PreferencesEntity::class],
    version = 2, // Increment version number
    exportSchema = false
)
@TypeConverters(DateConverter::class) // For DateTime handling
abstract class AppDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
    abstract fun itineraryDao(): ItineraryDao
    abstract fun userDao(): UserDao
    abstract fun authenticationDao(): AuthenticationDao
    abstract fun preferencesDao(): PreferencesDao

    companion object {
        // Singleton prevents multiple instances of database opening at the same time
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Migration from version 1 to 2 (adding new user fields)
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create a temporary table with the new schema
                database.execSQL(
                    """
                    CREATE TABLE users_new (
                        userId TEXT NOT NULL PRIMARY KEY,
                        email TEXT NOT NULL,
                        password TEXT NOT NULL,
                        firstName TEXT NOT NULL,
                        lastName TEXT NOT NULL,
                        username TEXT NOT NULL DEFAULT '',
                        birthDate INTEGER,
                        address TEXT,
                        country TEXT,
                        phoneNumber TEXT,
                        acceptEmails INTEGER NOT NULL DEFAULT 0,
                        imageURL TEXT
                    )
                    """
                )

                // Copy the data from the old table to the new table
                database.execSQL(
                    """
                    INSERT INTO users_new (
                        userId, email, password, firstName, lastName, imageURL
                    ) 
                    SELECT userId, email, password, firstName, lastName, imageURL 
                    FROM users
                    """
                )

                // Remove the old table
                database.execSQL("DROP TABLE users")

                // Rename the new table to the correct name
                database.execSQL("ALTER TABLE users_new RENAME TO users")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            // If the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "goplanify_database"
                )
                    .addMigrations(MIGRATION_1_2) // Add the migration
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}