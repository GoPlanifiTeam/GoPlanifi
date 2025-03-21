package com.example.travelplanner

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.example.travelplanner.ui.screens.LoginScreen
import com.example.travelplanner.ui.viewmodel.TripViewModel
import com.example.travelplanner.domain.repository.TripRepository

@Composable
fun NavGraph(navController: NavHostController) {
    val homeRoute = "Home" // "Home"
    val aboutRoute = "About" // "About"
    val versionRoute = "Version" // "Version"
    val profileRoute = "Profile" // "Profile"
    val settingsRoute = "Settings" // "Settings"
    val termsRoute = "Terms" // "Terms & Conditions"
    val itineraryRoute = "itineraryScreen/{tripId}" // Using tripId as parameter
    val tripsRoute = "tripsScreen" // Trips screen route
    val loginRoute = "loginScreen" // Trips screen route

    // Crea el tripViewModel aquí para ser accesible en todas las pantallas

    val tripViewModel: TripViewModel = viewModel()
    tripViewModel.fetchTrips()

    NavHost(navController = navController, startDestination = "loginScreen") {
        // Main Screen
        composable(homeRoute) { MainScreen(navController, tripViewModel) }

        // Other screens
        composable(aboutRoute) { AboutScreen(navController) }
        composable(versionRoute) { VersionScreen(navController) }
        composable(profileRoute) { ProfileScreen(navController) }
        composable(settingsRoute) { SettingsScreen(navController) }
        composable(termsRoute) { TermsAndConditionsScreen(navController) }
        composable(loginRoute) { LoginScreen(navController) }

        // Itinerary screen with tripId as a parameter, pass tripViewModel
        composable(itineraryRoute) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId")
            if (tripId != null) {
                ItineraryScreen(navController, tripId, tripViewModel) // Pasa el tripViewModel
            }
        }
        // Trips screen
        composable(tripsRoute) { TripsScreen(navController) }
    }
}
