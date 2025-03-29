package com.example.goplanify.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val userId: String,
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val imageURL: String?
)
