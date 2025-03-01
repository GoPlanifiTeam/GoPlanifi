package com.example.travelplanner.domain

data class User(
    var userId: String,
    var email: String,
    var password: String,
    var firstName: String,
    var lastName: String,
    val trips: List<Trip>,
    val imageURL: String
)
