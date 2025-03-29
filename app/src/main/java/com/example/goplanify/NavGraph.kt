package com.example.goplanify

import ItineraryScreen
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.goplanify.ui.screens.*
import com.example.goplanify.ui.viewmodel.AuthViewModel
import com.example.goplanify.ui.viewmodel.SettingsViewModel
import com.example.goplanify.ui.viewmodel.TripViewModel
import kotlinx.coroutines.launch

@Composable
fun NavGraph(navController: NavHostController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val tripViewModel: TripViewModel = hiltViewModel()
    val authViewModel: AuthViewModel = hiltViewModel()
    val settingsViewModel: SettingsViewModel = hiltViewModel()

    var languageReady by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val user = authViewModel.getUserById("test123")
            if (user != null) {
                Log.d("NavGraph", "Usuario encontrado: ${user.userId}")
                authViewModel.setCurrentUser(user)
                tripViewModel.getObjectUserTrips(user)

                val lang = settingsViewModel.getSavedLanguageFromRoom(user.userId)
                setLocale(context, lang)
                settingsViewModel.loadPreferences(user.userId) // Para que selectedLanguage se actualice

                Log.d("NavGraph", "Idioma aplicado: $lang")
                languageReady = true
            } else {
                Log.e("NavGraph", "Usuario test123 no encontrado en BD.")
                languageReady = true // Para evitar bloqueo si no se encuentra
            }
        }
    }

    if (!languageReady) {
    } else {
        // Solo mostrar la app cuando ya se aplicó el idioma
        val selectedLanguage = settingsViewModel.settingsState.collectAsState().value.selectedLanguage

        key(selectedLanguage) {
            NavHost(navController = navController, startDestination = "loginScreen") {
                composable("Home") { MainScreen(navController, tripViewModel) }
                composable("About") { AboutScreen(navController) }
                composable("Version") { VersionScreen(navController) }
                composable("Profile") { ProfileScreen(navController) }
                composable("Settings") { SettingsScreen(navController) }
                composable("Terms") { TermsAndConditionsScreen(navController) }
                composable("loginScreen") { LoginScreen(navController) }

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
        }
    }
}

