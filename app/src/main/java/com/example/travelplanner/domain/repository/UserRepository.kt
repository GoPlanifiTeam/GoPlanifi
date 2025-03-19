package com.example.travelplanner.domain.repository

import com.example.travelplanner.domain.model.User

class UserRepository {
    private val users = mutableListOf<User>()

    fun addUser(user: User) {
        users.add(user)
    }

    fun getUserById(userId: String): User? {
        return users.find { it.userId == userId }
    }

    fun updateUser(userId: String, updatedUser: User) {
        users.replaceAll { if (it.userId == userId) updatedUser else it }
    }
}
