package com.example.goplanify.domain.repository

import com.example.goplanify.domain.model.User
import java.util.regex.Pattern
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor() {
    private val users = mutableListOf<User>()
    private val emailPattern = Pattern.compile(
        "[a-zA-Z0-9+._%\\-]{1,256}" +
                "@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )

    fun addUser(user: User): Result<User> {
        return when {
            user.userId.isBlank() ->
                Result.failure(IllegalArgumentException("User ID cannot be empty"))
            user.email.isBlank() ->
                Result.failure(IllegalArgumentException("Email cannot be empty"))
            !isValidEmail(user.email) ->
                Result.failure(IllegalArgumentException("Invalid email format"))
            user.password.length < 8 ->
                Result.failure(IllegalArgumentException("Password must be at least 8 characters"))
            user.firstName.isBlank() ->
                Result.failure(IllegalArgumentException("First name cannot be empty"))
            user.lastName.isBlank() ->
                Result.failure(IllegalArgumentException("Last name cannot be empty"))
            getUserByEmail(user.email) != null ->
                Result.failure(IllegalArgumentException("Email already registered"))
            getUserById(user.userId) != null ->
                Result.failure(IllegalArgumentException("User ID already exists"))
            else -> {
                users.add(user)
                Result.success(user)
            }
        }
    }

    fun getUserById(userId: String): User? {
        return users.find { it.userId == userId }
    }

    fun getUserByEmail(email: String): User? {
        return users.find { it.email == email }
    }

    fun updateUser(userId: String, updatedUser: User): Result<User> {
        val index = users.indexOfFirst { it.userId == userId }
        return if (index != -1) {
            // Validate the updated user
            when {
                updatedUser.email.isBlank() ->
                    Result.failure(IllegalArgumentException("Email cannot be empty"))
                !isValidEmail(updatedUser.email) ->
                    Result.failure(IllegalArgumentException("Invalid email format"))
                updatedUser.firstName.isBlank() ->
                    Result.failure(IllegalArgumentException("First name cannot be empty"))
                updatedUser.lastName.isBlank() ->
                    Result.failure(IllegalArgumentException("Last name cannot be empty"))
                else -> {
                    users[index] = updatedUser
                    Result.success(updatedUser)
                }
            }
        } else {
            Result.failure(IllegalArgumentException("User not found"))
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return emailPattern.matcher(email).matches()
    }
}