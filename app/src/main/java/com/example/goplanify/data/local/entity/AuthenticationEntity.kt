package com.example.goplanify.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "authentications")
data class AuthenticationEntity(
    @PrimaryKey val userId: String,
    val loginErrors: Int
)
