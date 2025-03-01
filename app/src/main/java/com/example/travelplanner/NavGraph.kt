package com.example.travelplanner


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.travelplanner.ui.screens.AboutScreen
import com.example.travelplanner.ui.screens.mainAppPage
import com.example.travelplanner.ui.screens.ProfileScreen
import com.example.travelplanner.ui.screens.SettingsScreen
import com.example.travelplanner.ui.screens.VersionScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { mainAppPage(navController) }
        composable("about") { AboutScreen(navController) }
        composable("version") { VersionScreen(navController) }
        composable("profile") { ProfileScreen(navController) }
        composable("settings") { SettingsScreen(navController) }
    }
}