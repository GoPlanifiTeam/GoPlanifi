package com.example.goplanify

import ItineraryScreen
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.goplanify.domain.model.AuthState
import com.example.goplanify.ui.screens.*
import com.example.goplanify.ui.viewmodel.AuthViewModel
import com.example.goplanify.ui.viewmodel.SettingsViewModel
import com.example.goplanify.ui.viewmodel.TripViewModel
import com.example.goplanify.ui.screens.BookScreen
import com.example.goplanify.ui.screens.HotelDetailScreen
import com.example.goplanify.ui.screens.ReservationsScreen
import androidx.navigation.navArgument
import kotlinx.coroutines.launch

@Composable
fun NavGraph(navController: NavHostController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val tripViewModel: TripViewModel = hiltViewModel()
    val authViewModel: AuthViewModel = hiltViewModel()
    val settingsViewModel: SettingsViewModel = hiltViewModel()

    var languageReady by remember { mutableStateOf(false) }
    val currentUser by authViewModel.currentUser.collectAsState()
    val authState by authViewModel.authState.observeAsState()

    // Only show the app when language is applied or user is not authenticated
    val selectedLanguage = settingsViewModel.settingsState.collectAsState().value.selectedLanguage

    // Define a boolean to track if navigation has been performed
    var initialNavigationComplete by remember { mutableStateOf(false) }

    // First, render the NavHost
    NavHost(navController = navController, startDestination = "loginScreen") {
        // Your existing routes
        composable("Home") { MainScreen(navController, tripViewModel) }
        composable("About") { AboutScreen(navController) }
        composable("Version") { VersionScreen(navController) }
        composable("Profile") { ProfileScreen(navController) }
        composable("Settings") { SettingsScreen(navController) }
        composable("Terms") { TermsAndConditionsScreen(navController) }
        composable("loginScreen") { LoginScreen(navController) }
        composable("signupScreen") { SignupScreen(navController) }
        composable("book") { BookScreen(navController) }

        composable(
            route = "hotel/{hotelId}/{groupId}/{startDate}/{endDate}",
            arguments = listOf(
                navArgument("hotelId") { type = NavType.StringType },
                navArgument("groupId") { type = NavType.StringType },
                navArgument("startDate") { type = NavType.StringType },
                navArgument("endDate") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val hotelId = backStackEntry.arguments?.getString("hotelId") ?: ""
            val groupId = backStackEntry.arguments?.getString("groupId") ?: "G02"
            val startDate = backStackEntry.arguments?.getString("startDate") ?: ""
            val endDate = backStackEntry.arguments?.getString("endDate") ?: ""

            HotelDetailScreen(
                hotelId = hotelId,
                groupId = groupId,
                startDateStr = startDate,
                endDateStr = endDate,
                navController = navController
            )
        }

        composable("reservations") {
            ReservationsScreen(navController)
        }

        composable(
            route = "ItineraryScreen?tripId={tripId}",
            arguments = listOf(navArgument("tripId") {
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId")
            ItineraryScreen(navController = navController, tripId = tripId)
        }

        composable("tripsScreen") { TripsScreen(navController) }
    }

    // Now that NavHost is initialized, add the LaunchedEffect to handle navigation
    LaunchedEffect(authState, currentUser, initialNavigationComplete) {
        // Skip if we've already performed initial navigation
        if (initialNavigationComplete) return@LaunchedEffect

        Log.d("NavGraph", "Checking authentication state: $authState")

        // Handle different auth states
        when (authState) {
            is AuthState.Authenticated -> {
                currentUser?.let { user ->
                    Log.d("NavGraph", "Authenticated user found: ${user.userId}")

                    // Check email verification
                    val isEmailVerified = authViewModel.isEmailVerified()
                    Log.d("NavGraph", "Email verified: $isEmailVerified")

                    if (!isEmailVerified) {
                        // If email is not verified, stay on login screen
                        // No navigation needed, login is the start destination
                        Log.d("NavGraph", "Email not verified, staying on login screen")
                    } else {
                        // Email is verified, load language settings and navigate to Home
                        Log.d("NavGraph", "Email verified, proceeding with app initialization")

                        // Load user data and preferences
                        tripViewModel.getObjectUserTrips(user)
                        val lang = settingsViewModel.getSavedLanguageFromRoom(user.userId)
                        setLocale(context, lang)
                        settingsViewModel.loadPreferences(user.userId)

                        // Navigate to home
                        Log.d("NavGraph", "Navigating to Home")
                        navController.navigate("Home") {
                            popUpTo("loginScreen") { inclusive = true }
                        }
                    }
                }
            }
            is AuthState.EmailNotVerified -> {
                // Email not verified, stay on login screen
                Log.d("NavGraph", "Email not verified state detected")
                // No navigation needed, login is the start destination
            }
            is AuthState.Unauthenticated -> {
                Log.d("NavGraph", "User is unauthenticated, staying on login screen")
                // No navigation needed, login is the start destination
            }
            else -> {
                // For other states (loading, error), just wait
                Log.d("NavGraph", "Other auth state: $authState, waiting...")
                return@LaunchedEffect
            }
        }

        // Mark initial navigation as complete
        initialNavigationComplete = true
        languageReady = true
    }
}