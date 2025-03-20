package com.example.travelplanner.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.travelplanner.NavGraph
import com.example.travelplanner.R
import com.example.travelplanner.ui.viewmodel.ItineraryViewModel
import com.example.travelplanner.ui.viewmodel.TripViewModel
import androidx.compose.material.icons.filled.*
import java.util.UUID
import com.example.travelplanner.domain.model.Trip
import com.example.travelplanner.domain.model.Map
import com.example.travelplanner.domain.model.User
@Composable
fun MainScreen(navController: NavController) {
    val tripViewModel: TripViewModel = viewModel()
    val itineraryViewModel: ItineraryViewModel = viewModel()

    val testUser = User(
        userId = "testUser123",
        email = "testuser@example.com",
        password = "password",
        firstName = "Test",
        lastName = "User",
        trips = emptyList(),
        imageURL = "https://example.com/user-avatar.png"
    )

    val destinations = listOf(
        "Paris" to listOf("Visit Eiffel Tower", "Louvre Museum", "Seine River Cruise"),
        "New York" to listOf("Times Square", "Statue of Liberty", "Central Park"),
        "Tokyo" to listOf("Shibuya Crossing", "Mount Fuji Day Trip", "Akihabara Shopping"),
        "London" to listOf("Big Ben", "London Eye", "Tower of London"),
        "Rome" to listOf("Colosseum", "Vatican Museums", "Trevi Fountain")
    )

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
            Text(text = "Choose Your Travel Package", style = MaterialTheme.typography.headlineMedium)

            LazyColumn {
                items(destinations) { (destination, itineraries) ->
                    DestinationCard(destination, itineraries, navController, tripViewModel, itineraryViewModel, testUser)
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                val tripId = UUID.randomUUID().toString()

                val tripMap = Map(latitud = 0.0, longitud = 0.0, direction = "Unknown")

                val newTrip = Trip(
                    id = tripId,
                    destination = destination,
                    startDate = "2025-06-01",
                    endDate = "2025-06-10",
                    user = user,
                    map = tripMap,
                    itineraries = emptyList(),
                    images = emptyList(),
                    aiRecommendations = emptyList()
                )

                tripViewModel.addTrip(newTrip)

                itineraries.forEach { activity ->
                    itineraryViewModel.addItineraryItem(tripId, activity, destination)
                }

                navController.navigate("tripsScreen")
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Trip to $destination", style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Things to do:", style = MaterialTheme.typography.bodyLarge)
            itineraries.forEach { activity ->
                Text(text = "- $activity", style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val tripId = UUID.randomUUID().toString()

                    val tripMap = com.example.travelplanner.domain.model.Map(
                        latitud = 0.0,
                        longitud = 0.0,
                        direction = "Unknown"
                    )

                    val newTrip = Trip(
                        id = tripId,
                        destination = destination,
                        startDate = "2025-06-01",
                        endDate = "2025-06-10",
                        user = user,
                        map = tripMap,
                        itineraries = emptyList(),
                        images = emptyList(),
                        aiRecommendations = emptyList()
                    )

                    tripViewModel.addTrip(newTrip)

                    itineraries.forEach { activity ->
                        itineraryViewModel.addItineraryItem(tripId, activity, destination)
                    }

                    navController.navigate("tripsScreen")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Select and Add to My Trips")
            }
        }
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
