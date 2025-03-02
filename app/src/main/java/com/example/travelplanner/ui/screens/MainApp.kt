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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

// Enum para representar el modo seleccionado
enum class DisplayMode {
    Trip, Itinerary, UserPreferences
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun mainAppPage(navController: NavController) {
    // Estado que guarda el modo actual
    var selectedDisplayMode by remember { mutableStateOf(DisplayMode.Trip) }
    var showSettingsMenu by remember { mutableStateOf(false) }
    // Esto es la estructura para todas las paginas, de Top Bar y Bottom Bar
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Sección Izquierda: Botón de menú
                        IconButton(
                            onClick = {  },
                            modifier = Modifier.wrapContentSize()
                        ) {
                            Icon(Icons.Filled.Home, contentDescription = "Menu")
                        }

                        // Sección Central: Título (ocupa menos espacio)
                        Text(
                            text = "Home",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.weight(0.9f), // Ocupa menos espacio
                        )

                        // Sección Derecha: Configuración y menú desplegable (más espacio)
                        Box(
                            modifier = Modifier.weight(0.5f), // Ocupa más espacio
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            IconButton(onClick = { showSettingsMenu = !showSettingsMenu }) {
                                Icon(Icons.Outlined.Settings, contentDescription = "Settings")
                            }
                            DropdownMenu(
                                expanded = showSettingsMenu,
                                onDismissRequest = { showSettingsMenu = false }
                            ) {
                                DropdownMenuItem(
                                    leadingIcon = { Icon(Icons.Filled.Info, contentDescription = "About Icon") },
                                    text = { Text("About") },
                                    onClick = {
                                        showSettingsMenu = false
                                        navController.navigate("about")
                                    }
                                )
                                DropdownMenuItem(
                                    leadingIcon = { Icon(Icons.Filled.Build, contentDescription = "Version Icon") },
                                    text = { Text("Version") },
                                    onClick = {
                                        showSettingsMenu = false
                                        navController.navigate("version")
                                    }
                                )
                                DropdownMenuItem(
                                    leadingIcon = { Icon(Icons.Filled.Person, contentDescription = "Profile Icon") },
                                    text = { Text("Profile") },
                                    onClick = {
                                        showSettingsMenu = false
                                        navController.navigate("profile")
                                    }
                                )
                                DropdownMenuItem(
                                    leadingIcon = { Icon(Icons.Filled.Settings, contentDescription = "Settings Icon") },
                                    text = { Text("Settings") },
                                    onClick = {
                                        showSettingsMenu = false
                                        navController.navigate("settings")
                                    }
                                )
                            }
                        }
                    }
                }
            )
        },//FAB
        floatingActionButton = {
            FloatingActionButton(
                onClick = {  },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                //Icon(Icons.Filled.Add, contentDescription = "Add")
                Text("hi")
            }
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Menu, contentDescription = "Básica") },
                    selected = selectedDisplayMode == DisplayMode.Trip,
                    onClick = { selectedDisplayMode = DisplayMode.Trip },
                    label = { Text("Básica") }
                )
                //iconos
                //https://fonts.google.com/icons
                //https://developer.android.com/reference/kotlin/androidx/compose/material/icons/package-summary
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Scale, contentDescription = "IMC") },
                    selected = selectedDisplayMode == DisplayMode.Itinerary,
                    onClick = { selectedDisplayMode = DisplayMode.Itinerary },
                    label = { Text("IMC") }
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Default.FormatListNumbered, contentDescription = "LIST") },
                    selected = selectedDisplayMode == DisplayMode.UserPreferences,
                    onClick = { selectedDisplayMode = DisplayMode.UserPreferences },
                    label = { Text("LIST") }
                )
            }
        }
    ) { innerPadding ->
        // Contenido que se actualiza según el modo seleccionado
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
    // Vista placeholder para la calculadora básica
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Calculadora Básica", style = MaterialTheme.typography.headlineMedium )
        // Aquí puedes agregar los componentes y lógica de la calculadora básica
    }
}

@Composable
fun Itinerary() {
    // Vista placeholder para la calculadora de IMC
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Calculadora de IMC", style = MaterialTheme.typography.headlineMedium )
        // Aquí puedes agregar los componentes y lógica de la calculadora básica

    }
}


@Composable
fun UserPreferences() {
    // Vista placeholder para la calculadora de IMC
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Ejemplo de Listado", style = MaterialTheme.typography.headlineMedium )
        // Aquí puedes agregar los componentes y lógica de la calculadora básica

        ListApp()
    }
}