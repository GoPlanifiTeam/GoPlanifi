package com.example.goplanify.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val userId: String,
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val username: String,
    val birthDate: Date?,
    val address: String?,
    val country: String?,
    val phoneNumber: String?,
    val acceptEmails: Boolean,
    val imageURL: String?
)