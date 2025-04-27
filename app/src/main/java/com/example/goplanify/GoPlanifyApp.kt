package com.example.goplanify

import android.app.Application
import com.example.goplanify.utils.FirebaseUtils
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GoPlanifyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize Firebase
        FirebaseUtils.initializeFirebase(this)

        // Use Debug provider for development
        if (BuildConfig.DEBUG) {
            // For development only - this bypasses the reCAPTCHA check
            val firebaseAppCheck = FirebaseAppCheck.getInstance()
            firebaseAppCheck.installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance()
            )

            // You'll need to get the debug token and register it in Firebase Console
            // The token will be printed in your logs when you first run the app
        } else {
            // For production - use Play Integrity
            val firebaseAppCheck = FirebaseAppCheck.getInstance()
            firebaseAppCheck.installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance()
            )
        }
    }
}