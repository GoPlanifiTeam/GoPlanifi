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
import com.example.goplanify.data.local.dao.ItineraryImageDao
import com.example.goplanify.data.local.dao.PreferencesDao
import com.example.goplanify.data.local.dao.TripDao
import com.example.goplanify.data.local.dao.UserDao
import com.example.goplanify.data.local.entity.ItineraryItemEntity
import com.example.goplanify.data.local.entity.ItineraryImageEntity
import com.example.goplanify.data.local.entity.TripEntity
import com.example.goplanify.data.local.entity.UserEntity
import com.example.goplanify.data.local.entity.AuthenticationEntity
import com.example.goplanify.data.local.entity.PreferencesEntity

@Database(
    entities = [
        ItineraryItemEntity::class,
        TripEntity::class,
        UserEntity::class,
        AuthenticationEntity::class,
        PreferencesEntity::class,
        ItineraryImageEntity::class  // Nueva entidad añadida
    ],
    version = 3, // Incrementado a versión 3 para la nueva migración
    exportSchema = false
)
@TypeConverters(DateConverter::class) // For DateTime handling
abstract class AppDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
    abstract fun itineraryDao(): ItineraryDao
    abstract fun userDao(): UserDao
    abstract fun authenticationDao(): AuthenticationDao
    abstract fun preferencesDao(): PreferencesDao
    abstract fun itineraryImageDao(): ItineraryImageDao // Nuevo DAO añadido

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

        // Migration from version 2 to 3 (adding itinerary_images table)
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create the itinerary_images table
                database.execSQL(
                    """
                    CREATE TABLE itinerary_images (
                        id TEXT NOT NULL PRIMARY KEY,
                        itineraryId TEXT NOT NULL,
                        imagePath TEXT NOT NULL,
                        title TEXT,
                        description TEXT,
                        FOREIGN KEY (itineraryId) REFERENCES itinerary_items(id) ON DELETE CASCADE
                    )
                    """
                )

                // Crear índice para búsquedas más rápidas por itineraryId
                database.execSQL(
                    "CREATE INDEX index_itinerary_images_itineraryId ON itinerary_images(itineraryId)"
                )
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
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3) // Añadida la nueva migración
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}