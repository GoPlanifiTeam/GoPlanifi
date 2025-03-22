package com.example.goplanify.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import coil.compose.rememberAsyncImagePainter
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.goplanify.R
import com.example.goplanify.domain.model.User
import com.example.goplanify.ui.viewmodel.AuthViewModel
import com.example.goplanify.ui.viewmodel.ItineraryViewModel
import com.example.goplanify.ui.viewmodel.TripViewModel

@Composable
fun MainScreen(
    navController: NavController,
    tripViewModel: TripViewModel = hiltViewModel(),
    itineraryViewModel: ItineraryViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    Log.d("CurrentUser", "Usuario actual $currentUser")
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
                    trip.user?.let {
                        DestinationCard(
                            destination = trip.destination,
                            itineraries = trip.itineraries.map { it.name },
                            navController = navController,
                            tripViewModel = tripViewModel,
                            itineraryViewModel = itineraryViewModel,
                            user = it,
                            tripId = trip.id,
                            tripName = trip.destination
                        )
                    }
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
    user: User,
    tripId: String,
    tripName: String
) {
    val trip = tripViewModel.trips.collectAsState().value.find { it.destination == destination }

    trip?.let {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable {
                    navController.navigate("ItineraryScreen?tripId=$tripId")
                },
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = destination, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                val painter = rememberAsyncImagePainter(trip.imageURL)
                Image(
                    painter = painter,
                    contentDescription = "Trip Image for $destination",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .height(180.dp),
                    contentScale = ContentScale.Crop
                )
                Text(text = stringResource(R.string.itinerary), style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = "• ${trip.itineraries.first().name}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "🔎 Explore more...",
                    style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.padding(top = 4.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        navController.navigate("ItineraryScreen?tripId=$tripId")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(R.string.add_trip))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonTopBar(title: String, navController: NavController) {
    var showSettingsMenu by remember { mutableStateOf(false) }
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val maxCharacters = (screenWidth / 10) * 2

    val adjustedTitle = remember(title) {
        if (title.length > maxCharacters) {
            val midpoint = title.length / 2
            val splitIndex = title.indexOf(" ", midpoint).takeIf { it > 0 } ?: midpoint
            "${title.substring(0, splitIndex)}\n${title.substring(splitIndex).trim()}"
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
                    text = { Text(stringResource(R.string.aboutScreen)) },
                    onClick = {
                        showSettingsMenu = false
                        navController.navigate("about")
                    }
                )
                DropdownMenuItem(
                    leadingIcon = { Icon(Icons.Filled.Build, contentDescription = stringResource(R.string.versionScreen)) },
                    text = { Text(stringResource(R.string.versionScreen)) },
                    onClick = {
                        showSettingsMenu = false
                        navController.navigate("version")
                    }
                )
                DropdownMenuItem(
                    leadingIcon = { Icon(Icons.Filled.Person, contentDescription = stringResource(R.string.profileScreen)) },
                    text = { Text(stringResource(R.string.profileScreen)) },
                    onClick = {
                        showSettingsMenu = false
                        navController.navigate("profile")
                    }
                )
                DropdownMenuItem(
                    leadingIcon = { Icon(Icons.Filled.Settings, contentDescription = stringResource(R.string.settingsScreen)) },
                    text = { Text(stringResource(R.string.settingsScreen)) },
                    onClick = {
                        showSettingsMenu = false
                        navController.navigate("settings")
                    }
                )
                DropdownMenuItem(
                    leadingIcon = { Icon(Icons.Filled.Info, contentDescription = stringResource(R.string.terms_screen)) },
                    text = { Text(stringResource(R.string.terms_screen)) },
                    onClick = {
                        showSettingsMenu = false
                        navController.navigate("terms")
                    }
                )
            }
        }
    )
}