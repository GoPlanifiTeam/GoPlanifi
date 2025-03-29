package com.example.goplanify.data.local.mapper

import com.example.goplanify.data.local.entity.UserEntity
import com.example.goplanify.domain.model.Trip
import com.example.goplanify.domain.model.User

    fun User.toEntity(): UserEntity =
        UserEntity(
            userId = userId,
            email = email,
            password = password,
            firstName = firstName,
            lastName = lastName,
            imageURL = imageURL
        )

fun UserEntity.toDomain(trips: List<Trip> = emptyList()): User =
    User(
        userId = userId,
        email = email,
        password = password,
        firstName = firstName,
        lastName = lastName,
        imageURL = imageURL,
        trips = trips
    )





