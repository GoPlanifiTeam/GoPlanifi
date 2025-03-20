package com.example.travelplanner

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.travelplanner.ui.screens.AboutScreen
import com.example.travelplanner.ui.screens.ProfileScreen
import com.example.travelplanner.ui.screens.SettingsScreen
import com.example.travelplanner.ui.screens.VersionScreen
import com.example.travelplanner.ui.screens.TermsAndConditionsScreen
import com.example.travelplanner.ui.screens.ItineraryScreen
import com.example.travelplanner.ui.screens.TripsScreen
import com.example.travelplanner.ui.screens.MainScreen

@Composable
fun NavGraph(navController: NavHostController) {
    val homeRoute = stringResource(R.string.mainApp) // "Home"
    val aboutRoute = stringResource(R.string.aboutScreen) // "About"
    val versionRoute = stringResource(R.string.versionScreen) // "Version"
    val profileRoute = stringResource(R.string.profileScreen) // "Profile"
    val settingsRoute = stringResource(R.string.settingsScreen) // "Settings"
    val termsRoute = stringResource(R.string.termsAppScreen) // "Terms & Conditions"
    val itineraryRoute = "itineraryScreen/{tripId}" // Using tripId as parameter
    val tripsRoute = "tripsScreen" // Trips screen route

    NavHost(navController = navController, startDestination = "home") {
        // Main Screen
        composable(homeRoute) { MainScreen(navController) }

        // Other screens
        composable(aboutRoute) { AboutScreen(navController) }
        composable(versionRoute) { VersionScreen(navController) }
        composable(profileRoute) { ProfileScreen(navController) }
        composable(settingsRoute) { SettingsScreen(navController) }
        composable(termsRoute) { TermsAndConditionsScreen(navController) }

        // Itinerary screen with tripId as a parameter
        composable(itineraryRoute) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId")
            if (tripId != null) {
                ItineraryScreen(navController, tripId) // Pass the tripId to ItineraryScreen
            } else {
                // Handle invalid tripId or navigate back
                navController.navigateUp()
            }
        }

        // Trips screen
        composable(tripsRoute) { TripsScreen(navController) }
    }
}
