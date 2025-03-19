package com.example.travelplanner.domain.repository

import com.example.travelplanner.domain.model.User

class AuthenticationRepository {
    private val mockUserDatabase = mutableListOf<User>()

    fun login(user: User): Boolean {
        return mockUserDatabase.any { it == user }
    }

    fun logout(user: User): Boolean {
        // Logic to handle user logout
        return true
    }

    fun resetPassword(user: User): Boolean {
        // Logic to handle password reset
        return mockUserDatabase.contains(user)
    }
}