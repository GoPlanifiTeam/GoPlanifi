package com.example.travelplanner.domain.model

data class User(
    val userId: String,
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val imageURL: String
)