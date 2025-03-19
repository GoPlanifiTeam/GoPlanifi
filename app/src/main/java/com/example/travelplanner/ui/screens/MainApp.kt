package com.example.travelplanner.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.travelplanner.R

// Enum para representar el modo seleccionado
enum class DisplayMode {
    Trip, Itinerary, UserPreferences
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun mainAppPage(navController: NavController) {
    var selectedDisplayMode by remember { mutableStateOf(DisplayMode.Trip) }

    Scaffold(
        topBar = { CommonTopBar(title = stringResource(R.string.mainApp), navController) }, // ✅ Using `CommonTopBar`
        floatingActionButton = {
            FloatingActionButton(
                onClick = {  },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Text(stringResource(R.string.fab)) // "FAB"
            }
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Menu, contentDescription = stringResource(R.string.trip)) },
                    selected = selectedDisplayMode == DisplayMode.Trip,
                    onClick = { selectedDisplayMode = DisplayMode.Trip },
                    label = { Text(stringResource(R.string.trip)) } // "Trip"
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Scale, contentDescription = stringResource(R.string.itinerary)) },
                    selected = selectedDisplayMode == DisplayMode.Itinerary,
                    onClick = { selectedDisplayMode = DisplayMode.Itinerary },
                    label = { Text(stringResource(R.string.itinerary)) } // "Itinerary"
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.FormatListNumbered, contentDescription = stringResource(R.string.list)) },
                    selected = selectedDisplayMode == DisplayMode.UserPreferences,
                    onClick = { selectedDisplayMode = DisplayMode.UserPreferences },
                    label = { Text(stringResource(R.string.listExample)) } // "List"
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedDisplayMode) {
                DisplayMode.Trip -> Trip()
                DisplayMode.Itinerary -> Itinerary()
                DisplayMode.UserPreferences -> UserPreferences()
            }
        }
    }
}

@Composable
fun Trip() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(R.string.trip), style = MaterialTheme.typography.headlineMedium ) // "Trip"
    }
}

@Composable
fun Itinerary() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(R.string.itinerary), style = MaterialTheme.typography.headlineMedium ) // "Itinerary"
    }
}

@Composable
fun UserPreferences() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(R.string.listExample), style = MaterialTheme.typography.headlineMedium ) // "User Preferences"
        ListApp()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonTopBar(title: String, navController: NavController) {
    var showSettingsMenu by remember { mutableStateOf(false) }
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val context = LocalContext.current
    val maxCharacters = (screenWidth / 10) * 2 // Dynamic character limit based on screen width

    // 🔍 Check if the title is too long and dynamically adjust
    val adjustedTitle = remember(title) {
        if (title.length > maxCharacters) {
            val midpoint = title.length / 2
            val splitIndex = title.indexOf(" ", midpoint).takeIf { it > 0 } ?: midpoint
            "${title.substring(0, splitIndex)}\n${title.substring(splitIndex).trim()}" // Breaks into two lines
        } else {
            title
        }
    }

    // 🛠️ Retrieve typography **inside the Composable scope**
    val textStyle = if (adjustedTitle.length > maxCharacters) {
        MaterialTheme.typography.titleLarge // 🔥 Shrinks if too long
    } else {
        MaterialTheme.typography.headlineMedium // ✅ Default size
    }

    TopAppBar(
        title = {
            Text(
                text = adjustedTitle,
                style = textStyle, // 🔥 Dynamically adjusted size
                maxLines = 2 // Allows breaking into two lines if necessary
            )
        },
        navigationIcon = {
            IconButton(onClick = { navController.navigate("home") }) {
                Icon(Icons.Filled.Home, contentDescription = stringResource(R.string.mainApp)) // "Home"
            }
        },
        actions = {
            IconButton(onClick = { showSettingsMenu = !showSettingsMenu }) {
                Icon(Icons.Outlined.Settings, contentDescription = stringResource(R.string.settings)) // "Settings"
            }
            DropdownMenu(
                expanded = showSettingsMenu,
                onDismissRequest = { showSettingsMenu = false }
            ) {
                DropdownMenuItem(
                    leadingIcon = { Icon(Icons.Filled.Info, contentDescription = stringResource(R.string.aboutScreen)) },
                    text = { Text(stringResource(R.string.aboutScreen)) }, // "About"
                    onClick = {
                        showSettingsMenu = false
                        navController.navigate("about")
                    }
                )
                DropdownMenuItem(
                    leadingIcon = { Icon(Icons.Filled.Build, contentDescription = stringResource(R.string.versionScreen)) },
                    text = { Text(stringResource(R.string.versionScreen)) }, // "Version"
                    onClick = {
                        showSettingsMenu = false
                        navController.navigate("version")
                    }
                )
                DropdownMenuItem(
                    leadingIcon = { Icon(Icons.Filled.Person, contentDescription = stringResource(R.string.profileScreen)) },
                    text = { Text(stringResource(R.string.profileScreen)) }, // "Profile"
                    onClick = {
                        showSettingsMenu = false
                        navController.navigate("profile")
                    }
                )
                DropdownMenuItem(
                    leadingIcon = { Icon(Icons.Filled.Settings, contentDescription = stringResource(R.string.settingsScreen)) },
                    text = { Text(stringResource(R.string.settingsScreen)) }, // "Settings"
                    onClick = {
                        showSettingsMenu = false
                        navController.navigate("settings")
                    }
                )
                DropdownMenuItem(
                    leadingIcon = { Icon(Icons.Filled.Info, contentDescription = stringResource(R.string.terms_screen)) },
                    text = { Text(stringResource(R.string.terms_screen)) }, // "Terms & Conditions"
                    onClick = {
                        showSettingsMenu = false
                        navController.navigate("terms")
                    }
                )
            }
        }
    )
}
