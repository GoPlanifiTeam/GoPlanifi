package com.example.goplanify.data.local.dao

import androidx.room.*
import com.example.goplanify.data.local.entity.AuthenticationEntity

@Dao
interface AuthenticationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(auth: AuthenticationEntity)

    @Query("SELECT * FROM authentications WHERE userId = :userId")
    suspend fun getByUserId(userId: String): AuthenticationEntity?
}
