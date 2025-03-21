package com.example.travelplanner.ui.screens

import android.util.Log
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
import com.example.travelplanner.domain.repository.TripRepository

@Composable
fun MainScreen(navController: NavController, tripViewModel: TripViewModel) {
    val tripRepository = TripRepository() // Usamos TripRepository para manejar los viajes
    val itineraryViewModel: ItineraryViewModel = viewModel()

    val trips = tripRepository.getTrips()  // Obtener la lista de viajes desde el repositorio

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
                items(trips) { trip -> // Usamos los viajes obtenidos del repositorio
                    DestinationCard(
                        destination = trip.destination,
                        itineraries = trip.itineraries.map { it.name },
                        navController = navController,
                        tripViewModel = tripViewModel,  // Pasa el TripViewModel a DestinationCard
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
    tripViewModel: TripViewModel, // Recibe el tripViewModel aquí
    itineraryViewModel: ItineraryViewModel,
    user: User
) {
    val trip = tripViewModel.trips.collectAsState().value.find { it.destination == destination } // Buscar el viaje por destino

    if (trip != null) { // Si el viaje ya existe, navegamos a la pantalla de itinerarios
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable {
                    // Navegar al ItineraryScreen con el tripId del viaje existente
                    navController.navigate("ItineraryScreen/${trip.id}")
                },
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Trip to ${trip.destination}", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Things to do:", style = MaterialTheme.typography.bodyLarge)
                trip.itineraries.forEach { activity ->
                    Text(text = "- ${activity.name}", style = MaterialTheme.typography.bodyMedium)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        // Si es necesario navegar o realizar alguna acción al hacer clic en el botón
                        navController.navigate("ItineraryScreen/${trip.id}")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Select and Add to My Trips")
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
