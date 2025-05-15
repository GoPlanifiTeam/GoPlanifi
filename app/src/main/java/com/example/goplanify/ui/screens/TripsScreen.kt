package com.example.goplanify.ui.screens

import android.app.DatePickerDialog
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.goplanify.R
import com.example.goplanify.domain.model.ItineraryImage
import com.example.goplanify.domain.model.Trip
import com.example.goplanify.ui.viewmodel.AuthViewModel
import com.example.goplanify.ui.viewmodel.ItineraryViewModel
import com.example.goplanify.ui.viewmodel.SettingsViewModel
import com.example.goplanify.ui.viewmodel.TripViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TripsScreen(
    navController: NavController,
    tripViewModel: TripViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    itineraryViewModel: ItineraryViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val trips by tripViewModel.userTrips.collectAsState()

    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            tripViewModel.getObjectUserTrips(user)
            val lang = settingsViewModel.getSavedLanguageFromRoom(user.userId)
            Log.d("PREFERENCES-LANG", "User ${user.userId} prefers language: $lang")
        }
    }

    if (currentUser == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = stringResource(R.string.no_user_logged_in))
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { navController.navigate("loginScreen") }) {
                    Text(text = "Login")
                }
            }
        }
        return
    }

    val context = LocalContext.current
    val currentLocale = context.resources.configuration.locales[0]

    LaunchedEffect(Unit) {
        Log.d("LANGUAGE-CHECK", "Current language in TripsScreen: ${currentLocale.language}")
    }

    Scaffold(
        topBar = { CommonTopBar(title = stringResource(R.string.Trips), navController) },
        bottomBar = { BottomBar(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.listExample),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (trips.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_trips_found),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(trips) { trip ->
                        TripCard(
                            trip = trip,
                            navController = navController,
                            tripViewModel = tripViewModel,
                            itineraryViewModel = itineraryViewModel
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TripCard(
    trip: Trip,
    navController: NavController,
    tripViewModel: TripViewModel,
    itineraryViewModel: ItineraryViewModel
) {
    var showDateDialog by remember { mutableStateOf(false) }
    var newStartDate by remember { mutableStateOf(trip.startDate) }
    var newEndDate by remember { mutableStateOf(trip.endDate) }
    var updatedItineraries by remember { mutableStateOf(trip.itineraries) }

    // Estado para mostrar imagen a pantalla completa
    var selectedImage by remember { mutableStateOf<String?>(null) }

    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    fun stringToDate(dateString: String?): Date? = try { dateFormat.parse(dateString ?: "") } catch (_: Exception) { null }
    fun isDateInFuture(date: String): Boolean = stringToDate(date)?.after(Calendar.getInstance().time) == true

// Dialog para mostrar imagen a pantalla completa
    if (selectedImage != null) {
        AlertDialog(
            onDismissRequest = { selectedImage = null },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false
            ),
            modifier = Modifier
                .fillMaxWidth(0.85f)       // 85% del ancho de la pantalla
                .fillMaxHeight(0.7f)       // 70% de la altura de la pantalla
                .padding(16.dp),
            title = null,
            text = {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = rememberAsyncImagePainter(File(selectedImage!!)),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            },
            confirmButton = {
                IconButton(
                    onClick = { selectedImage = null },
                    modifier = Modifier
                        .padding(8.dp)
                        .background(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                            RoundedCornerShape(percent = 50)
                        )
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar")
                }
            },
            dismissButton = null
        )
    }

    if (showDateDialog) {
        AlertDialog(
            onDismissRequest = { showDateDialog = false },
            confirmButton = {
                Button(onClick = {
                    val validStart = isDateInFuture(newStartDate)
                    val validEnd = isDateInFuture(newEndDate)
                    val startDateParsed = stringToDate(newStartDate)
                    val endDateParsed = stringToDate(newEndDate)

                    val itinerariesValid = updatedItineraries.all {
                        val d = stringToDate(it.startDate)
                        d != null && d.after(Calendar.getInstance().time) && d >= startDateParsed && d <= endDateParsed
                    }

                    if (validStart && validEnd && startDateParsed != null && endDateParsed != null && startDateParsed <= endDateParsed && itinerariesValid) {
                        updatedItineraries.forEach { itinerary ->
                            itineraryViewModel.updateItineraryDates(
                                itinerary.id,
                                itinerary.startDate,
                                itinerary.startDate
                            )
                        }

                        val updatedTrip = trip.copy(
                            startDate = newStartDate,
                            endDate = newEndDate,
                            itineraries = updatedItineraries
                        )

                        tripViewModel.updateTrip(updatedTrip)
                        showDateDialog = false
                        navController.popBackStack()
                        navController.navigate("tripsScreen")
                    }
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                Button(onClick = { showDateDialog = false }) {
                    Text("Cancel")
                }
            },
            title = { Text(stringResource(R.string.edit_trip_title)) },
            text = {
                Column {
                    DatePickerInline(newStartDate, { newStartDate = it }, stringResource(R.string.trip_start_date_label))
                    Spacer(modifier = Modifier.height(8.dp))
                    DatePickerInline(newEndDate, { newEndDate = it }, stringResource(R.string.trip_end_date_label))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(stringResource(R.string.itinerary_label), style = MaterialTheme.typography.titleSmall)
                    updatedItineraries.forEachIndexed { index, itinerary ->
                        var itineraryStart by remember { mutableStateOf(itinerary.startDate) }
                        Text(text = "🏷️ ${itinerary.name} - ${itinerary.location}")
                        DatePickerInline(
                            currentDate = itineraryStart,
                            onDateSelected = {
                                if (isDateInFuture(it) && it >= newStartDate && it <= newEndDate) {
                                    itineraryStart = it
                                    updatedItineraries = updatedItineraries.toMutableList().also {
                                        it[index] = it[index].copy(startDate = itineraryStart, endDate = itineraryStart)
                                    }
                                }
                            },
                            label = "Itinerary Date"
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("🌍 ${stringResource(R.string.destination)}: ${trip.destination}", style = MaterialTheme.typography.titleMedium)
            Text("📅 ${stringResource(R.string.trip_start_date)}: ${trip.startDate}")
            Text("📅 ${stringResource(R.string.trip_end_date)}: ${trip.endDate}")
            Spacer(modifier = Modifier.height(8.dp))

            // Itinerarios con sus imágenes
            trip.itineraries.forEach { itinerary ->
                // Encabezado del itinerario
                Text("🏷️ ${itinerary.name} - ${itinerary.location}", style = MaterialTheme.typography.titleSmall)
                Text("📅 ${itinerary.startDate}", style = MaterialTheme.typography.bodySmall)

                // Mostrar imágenes del itinerario si existen
                if (itinerary.images != null && itinerary.images.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp)
                            .padding(vertical = 4.dp)
                    ) {
                        items(itinerary.images) { image ->
                            ItineraryImageThumbnail(
                                imagePath = image.imagePath,
                                onClick = { selectedImage = image.imagePath }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { showDateDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("🛠️ ${stringResource(R.string.edit_dates)}")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        tripViewModel.deleteTrip(trip.id)
                        navController.popBackStack()
                        navController.navigate("tripsScreen")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("🗑️ ${stringResource(R.string.delete)}")
                }
            }
        }
    }
}

@Composable
fun ItineraryImageThumbnail(imagePath: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .size(60.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = rememberAsyncImagePainter(File(imagePath)),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun DatePickerInline(
    currentDate: String,
    onDateSelected: (String) -> Unit,
    label: String
) {
    val context = LocalContext.current
    val selectedDate = remember { mutableStateOf(currentDate) }
    val datePickerDialog = remember { mutableStateOf<DatePickerDialog?>(null) }

    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    LaunchedEffect(Unit) {
        datePickerDialog.value = DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                val formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDayOfMonth)
                selectedDate.value = formattedDate
                onDateSelected(formattedDate)
            },
            year, month, day
        )
    }

    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = label)
        Button(onClick = { datePickerDialog.value?.show() }) {
            Text(text = selectedDate.value.ifEmpty { stringResource(R.string.select_date) })
        }
    }
}

@Composable
fun BottomBar(navController: NavController) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Menu, contentDescription = stringResource(R.string.trip)) },
            selected = false,
            onClick = { navController.navigate("home") },
            label = { Text(stringResource(R.string.menu)) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.FormatListNumbered, contentDescription = stringResource(R.string.itinerary)) },
            selected = false,
            onClick = { navController.navigate("ItineraryScreen") },
            label = { Text(stringResource(R.string.list)) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Person, contentDescription = stringResource(R.string.myTrips)) },
            selected = false,
            onClick = { navController.navigate("tripsScreen") },
            label = { Text(stringResource(R.string.profileScreen)) }
        )
    }
}