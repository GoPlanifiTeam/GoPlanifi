package com.example.travelplanner


import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.travelplanner.ui.screens.AboutScreen
import com.example.travelplanner.ui.screens.mainAppPage
import com.example.travelplanner.ui.screens.ProfileScreen
import com.example.travelplanner.ui.screens.SettingsScreen
import com.example.travelplanner.ui.screens.VersionScreen
import com.example.travelplanner.ui.screens.TermsAndConditionsScreen

@Composable
fun NavGraph(navController: NavHostController) {
    val homeRoute = stringResource(R.string.mainApp) // Get localized route name
    val aboutRoute = stringResource(R.string.aboutScreen) // Get localized route name
    val versionRoute = stringResource(R.string.versionScreen) // Get localized route name
    val profileRoute = stringResource(R.string.profileScreen) // Get localized route name
    val settingsRoute = stringResource(R.string.settingsScreen) // Get localized route name
    val termsRoute = stringResource(R.string.termsAppScreen) // Get localized route name

    NavHost(navController = navController, startDestination = "home") {

        composable(homeRoute) { mainAppPage(navController) }
        composable(aboutRoute) { AboutScreen(navController) }
        composable(versionRoute) { VersionScreen(navController) }
        composable(profileRoute) { ProfileScreen(navController) }
        composable(settingsRoute) { SettingsScreen(navController) }
        composable(termsRoute) { TermsAndConditionsScreen(navController) }
    }
}