package com.example.goplanify.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.goplanify.domain.model.User
import com.example.goplanify.ui.viewmodel.ItineraryViewModel
import com.example.goplanify.ui.viewmodel.TripViewModel
import com.example.goplanify.R


@Composable
fun MainScreen(
    navController: NavController,
    tripViewModel: TripViewModel = hiltViewModel(),
    itineraryViewModel: ItineraryViewModel = hiltViewModel()
) {
    val trips by tripViewModel.trips.collectAsState()
    
    LaunchedEffect(Unit) {
        tripViewModel.fetchTrips()
    }

    Scaffold(
        topBar = { CommonTopBar(title = stringResource(R.string.mainApp), navController) },
        bottomBar = { BottomBar(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(text = stringResource(R.string.trip), style = MaterialTheme.typography.headlineMedium)

            LazyColumn {
                items(trips) { trip ->
                    DestinationCard(
                        destination = trip.destination,
                        itineraries = trip.itineraries.map { it.name },
                        navController = navController,
                        tripViewModel = tripViewModel,
                        itineraryViewModel = itineraryViewModel,
                        user = trip.user
                    )
                }
            }
        }
    }
}


@Composable
fun DestinationCard(
    destination: String,
    itineraries: List<String>,
    navController: NavController,
    tripViewModel: TripViewModel,
    itineraryViewModel: ItineraryViewModel,
    user: User
) {
    val trip = tripViewModel.trips.collectAsState().value.find { it.destination == destination }

    if (trip != null) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable {
                    navController.navigate("ItineraryScreen/${trip.id}")
                },
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = stringResource(R.string.trip_destination, trip.destination), style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))

                Text(text = stringResource(R.string.itinerary), style = MaterialTheme.typography.bodyLarge)
                trip.itineraries.forEach { activity ->
                    Text(text = "- ${activity.name}", style = MaterialTheme.typography.bodyMedium)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        navController.navigate("ItineraryScreen/${trip.id}")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(R.string.add_trip))
                }
            }
        }
    } else {
        // Si no existe el viaje, puedes mostrar un mensaje o permitir crear uno nuevo
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

    val textStyle = if (adjustedTitle.length > maxCharacters) {
        MaterialTheme.typography.titleLarge
    } else {
        MaterialTheme.typography.headlineMedium
    }

    TopAppBar(
        title = {
            Text(
                text = adjustedTitle,
                style = textStyle,
                maxLines = 2
            )
        },
        navigationIcon = {
            IconButton(onClick = { navController.navigate("home") }) {
                Icon(Icons.Filled.Home, contentDescription = stringResource(R.string.mainApp))
            }
        },
        actions = {
            IconButton(onClick = { showSettingsMenu = !showSettingsMenu }) {
                Icon(Icons.Outlined.Settings, contentDescription = stringResource(R.string.settings))
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

