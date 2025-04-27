package com.example.goplanify.utils

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Utility class for Firebase related operations
 */
object FirebaseUtils {

    /**
     * Initialize Firebase in the application
     */
    fun initializeFirebase(context: Context) {
        try {
            FirebaseApp.initializeApp(context)
        } catch (e: Exception) {
            // Firebase already initialized
        }
    }

    /**
     * Get the current FirebaseUser or null if not authenticated
     */
    fun getCurrentUser(): FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
    }

    /**
     * Check if user's email is verified
     */
    fun isEmailVerified(): Boolean {
        return getCurrentUser()?.isEmailVerified ?: false
    }

    /**
     * Get current user ID or null if not authenticated
     */
    fun getCurrentUserId(): String? {
        return getCurrentUser()?.uid
    }

    /**
     * Helper to update user profile data in Firestore
     */
    suspend fun updateUserProfile(userId: String, data: Map<String, Any>): Boolean {
        return try {
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .set(data)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Helper to get user profile data from Firestore
     */
    suspend fun getUserProfile(userId: String): Map<String, Any>? {
        return try {
            val document = FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .get()
                .await()

            if (document.exists()) {
                document.data
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}