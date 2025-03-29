package com.example.goplanify.data.local.dao

import androidx.room.*
import com.example.goplanify.data.local.entity.PreferencesEntity

@Dao
interface PreferencesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(preferences: PreferencesEntity)

    @Query("SELECT * FROM preferences WHERE userId = :userId")
    suspend fun getByUserId(userId: String): PreferencesEntity?
}
