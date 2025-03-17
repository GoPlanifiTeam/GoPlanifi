package com.example.travelplanner.data.service

import com.example.travelplanner.domain.model.User
import com.example.travelplanner.domain.service.AuthenticationService

class AuthenticationServiceImpl : AuthenticationService {
    // In-memory storage for testing purposes
    private val users = mutableMapOf<String, User>()
    private val loggedInUsers = mutableSetOf<String>()
    private val loginAttempts = mutableMapOf<String, Int>()

    override fun login(user: User): Boolean {
        val storedUser = users[user.userId] ?: return false

        // Check if credentials match
        if (storedUser.email == user.email && storedUser.password == user.password) {
            loggedInUsers.add(user.userId)
            loginAttempts.remove(user.userId)
            return true
        }

        // Track failed login attempts
        loginAttempts[user.userId] = (loginAttempts[user.userId] ?: 0) + 1
        return false
    }

    override fun logout(user: User): Boolean {
        return loggedInUsers.remove(user.userId)
    }

    override fun resetPassword(user: User): Boolean {
        val storedUser = users[user.userId] ?: return false

        // In a real implementation, this would generate a reset token and send an email
        users[user.userId] = storedUser.copy(password = user.password)
        return true
    }

    // Additional helper methods
    fun registerUser(user: User): Boolean {
        if (users.containsKey(user.userId) || users.values.any { it.email == user.email }) {
            return false
        }
        users[user.userId] = user
        return true
    }

    fun isUserLoggedIn(userId: String): Boolean {
        return userId in loggedInUsers
    }

    fun getLoginAttempts(userId: String): Int {
        return loginAttempts[userId] ?: 0
    }
}