package com.example.travelplanner.classes

data class User(
    var userId: String,
    var email: String,
    var password: String,
    var firstName: String,
    var lastName: String,
    val itineraries: List<itineraryItem>,
    val imageURL: String
)
