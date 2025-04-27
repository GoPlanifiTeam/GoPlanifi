package com.example.goplanify.domain.repository

import com.example.goplanify.domain.model.User

interface UserRepository {
    suspend fun addUser(user: User): Result<User>
    suspend fun getUserById(userId: String): User?
    suspend fun getUserByEmail(email: String): User?
    suspend fun getUserByUsername(username: String): User?
    suspend fun updateUser(userId: String, updatedUser: User): Result<User>
    suspend fun isUsernameTaken(username: String): Boolean
}