import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.goplanify.ui.viewmodel.ItineraryViewModel
import com.example.goplanify.ui.viewmodel.TripViewModel
import com.example.goplanify.R
import com.example.goplanify.domain.model.Image
import com.example.goplanify.domain.model.ItineraryImage
import com.example.goplanify.domain.model.ItineraryItem
import com.example.goplanify.domain.model.Trip
import com.example.goplanify.ui.screens.BottomBar
import com.example.goplanify.ui.screens.CommonTopBar
import com.example.goplanify.ui.utils.ImageUtils
import com.example.goplanify.utils.*
import com.example.goplanify.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

// Componente para mostrar el selector de fecha
@Composable
fun DatePicker(
    onDateSelected: (String) -> Unit,
    currentDate: String,
    tripStartDate: String,
    tripEndDate: String
) {
    // Código existente sin cambios
    val context = LocalContext.current
    val datePickerDialog = remember { mutableStateOf<DatePickerDialog?>(null) }
    val selectedDate = remember { mutableStateOf(currentDate) }

    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)


    LaunchedEffect(Unit) {
        datePickerDialog.value = DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                val selectedFormattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDayOfMonth)
                selectedDate.value = selectedFormattedDate
                onDateSelected(selectedFormattedDate)
            },
            year,
            month,
            dayOfMonth
        )
    }

    Button(onClick = { datePickerDialog.value?.show() }) {
        Text(text = selectedDate.value.ifEmpty { stringResource(R.string.add) })
    }
}

// Componente para mostrar una miniatura de imagen
@Composable
fun ImageThumbnail(imagePath: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .size(60.dp)
            .clip(RoundedCornerShape(8.dp))  // Añadido clip con bordes redondeados como en TripsScreen
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

// Componente para mostrar un placeholder cuando no hay imágenes
@Composable
fun ImagePlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.PhotoLibrary,
                contentDescription = null,
                modifier = Modifier.size(36.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.no_images_added),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@SuppressLint("SimpleDateFormat")
@Composable
fun ItineraryScreen(
    navController: NavController,
    tripId: String? = null,
    tripViewModel: TripViewModel = hiltViewModel(),
    itineraryViewModel: ItineraryViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val trips by tripViewModel.userTrips.collectAsState()
    val allTrips by tripViewModel.trips.collectAsState()
    val selectedItineraries by itineraryViewModel.selectedItineraries.collectAsState()

    // Mapa para almacenar las imágenes de cada itinerario
    var itineraryImagesMap by remember { mutableStateOf<Map<String, List<Uri>>>(emptyMap()) }

    // Estado para almacenar las imágenes ya procesadas (ruta local)
    var processedImagesMap by remember { mutableStateOf<Map<String, List<ItineraryImage>>>(emptyMap()) }

    // Estado para mostrar imagen a pantalla completa
    var selectedImage by remember { mutableStateOf<Pair<String, String>?>(null) }

    val context = LocalContext.current

    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            tripViewModel.getObjectUserTrips(user)
        }
    }

    // Actualiza la UI cuando cambia el mapa de imágenes procesadas
    LaunchedEffect(processedImagesMap) {
        if (tripId != null) {
            currentUser?.let { user ->
                tripViewModel.getObjectUserTrips(user)
            }
        } else if (currentUser != null) {
            tripViewModel.getObjectUserTrips(currentUser!!)
        }
    }

    // If no user is authenticated, show login prompt
    if (currentUser == null) {
        Scaffold(
            topBar = { CommonTopBar(title = stringResource(R.string.itinerary), navController) },
            bottomBar = { BottomBar(navController) }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
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
        }
        return
    }

    val trip = allTrips.find { it.id == tripId }
    Log.d("User Detected","$currentUser")
    val itineraries: List<ItineraryItem> = when {
        tripId != null && trip != null -> {
            Log.d("ItineraryScreen", "Showing itineraries for tripId $tripId: ${trip.itineraries}")
            trip.itineraries
        }
        currentUser != null -> {
            val userTrips = trips.filter { it.user?.userId == currentUser!!.userId }
            Log.d("ItineraryScreen", "User ${currentUser!!.userId} has ${userTrips.size} trips")
            userTrips.flatMap {
                Log.d("ItineraryScreen", "Trip ${it.id} itineraries: ${it.itineraries}")
                it.itineraries
            }
        }
        else -> {
            Log.d("ItineraryScreen", "No user or trip found.")
            emptyList()
        }
    }

    // Inicializar el mapa de imágenes procesadas con las imágenes existentes
    LaunchedEffect(itineraries) {
        val initialImagesMap = mutableMapOf<String, List<ItineraryImage>>()

        itineraries.forEach { itinerary ->
            // Si el itinerario tiene imágenes, añadirlas al mapa
            if (itinerary.images != null && itinerary.images.isNotEmpty()) {
                Log.d("ItineraryScreen", "Cargando ${itinerary.images.size} imágenes para itinerario ${itinerary.name}")
                itinerary.images.forEach { img ->
                    Log.d("ItineraryScreen", "Imagen path: ${img.imagePath}")
                    // Verificar si el archivo existe
                    val file = File(img.imagePath)
                    if (file.exists()) {
                        Log.d("ItineraryScreen", "Archivo existe: ${file.absolutePath}")
                    } else {
                        Log.d("ItineraryScreen", "¡Alerta! Archivo no existe: ${file.absolutePath}")
                    }
                }
                initialImagesMap[itinerary.id] = itinerary.images
            }
        }

        // Actualizar el estado con las imágenes existentes
        processedImagesMap = initialImagesMap
        Log.d("ItineraryScreen", "ProcessedImagesMap inicializado con ${initialImagesMap.size} entradas")
    }

    var itineraryDates by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var tripStartDate by remember { mutableStateOf(trip?.startDate ?: "") }
    var tripEndDate by remember { mutableStateOf(trip?.endDate ?: "") }
    var itineraryDatesValid by remember { mutableStateOf<Map<String, Boolean>>(emptyMap()) }
    var isTripStartDateValid by remember { mutableStateOf(true) }
    var isTripEndDateValid by remember { mutableStateOf(true) }
    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    fun stringToDate(dateString: String?): Date? = try { dateFormat.parse(dateString ?: "") } catch (_: Exception) { null }
    fun isDateInFuture(date: String): Boolean = stringToDate(date)?.after(Calendar.getInstance().time) == true
    fun isDateInTripRange(date: String, startDate: String, endDate: String): Boolean {
        val dateParsed = stringToDate(date)
        val start = stringToDate(startDate)
        val end = stringToDate(endDate)
        return if (dateParsed != null && start != null && end != null) {
            !dateParsed.before(start) && !dateParsed.after(end)
        } else {
            false
        }
    }

    // Inicializar fechas de itinerarios
    LaunchedEffect(itineraries) {
        val dates = mutableMapOf<String, String>()
        itineraries.forEach { itinerary ->
            if (itinerary.startDate.isNotEmpty()) {
                dates[itinerary.id] = itinerary.startDate
            }
        }
        itineraryDates = dates
    }

    // Dialog para mostrar imagen a pantalla completa
    if (selectedImage != null) {
        val (itineraryId, imagePath) = selectedImage!!
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
                        painter = rememberAsyncImagePainter(File(imagePath)),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Botón para cerrar
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

                    // Botón para eliminar la imagen
                    IconButton(
                        onClick = {
                            // 1. Eliminar la imagen del mapa de imágenes procesadas
                            val currentImages = processedImagesMap[itineraryId] ?: emptyList()
                            processedImagesMap = processedImagesMap + (itineraryId to currentImages.filter { it.imagePath != imagePath })

                            // 2. Eliminar el archivo físico
                            ImageUtils.deleteImage(imagePath)

                            // 3. Eliminar la referencia de la base de datos
                            val imageToDelete = currentImages.find { it.imagePath == imagePath }
                            imageToDelete?.let { image ->
                                // Llamar al ViewModel para eliminar la imagen de la base de datos
                                itineraryViewModel.deleteItineraryImage(image.id)
                                Log.d("ItineraryScreen", "Eliminando imagen ID: ${image.id}")
                            }

                            selectedImage = null

                            // 4. Notificar al ViewModel que actualice los itinerarios
                            currentUser?.let { tripViewModel.getObjectUserTrips(it) }
                        },
                        modifier = Modifier
                            .padding(8.dp)
                            .background(
                                Color.Red.copy(alpha = 0.7f),
                                RoundedCornerShape(percent = 50)
                            )
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.White)
                    }
                }
            },
            dismissButton = null
        )
    }

    Scaffold(
        topBar = { CommonTopBar(title = stringResource(R.string.itinerary), navController) },
        bottomBar = { BottomBar(navController) }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Contenido de LazyColumn que ya existe - lo dejo igual
            item {
                if (tripId != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            trip?.let {
                                Text(text = "✈️ Trip to ${it.destination}", style = MaterialTheme.typography.titleLarge)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "🆔 Trip ID: $tripId", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    Text(
                        text = "📍 Choose your itinerary items:",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                } else {
                    Text(text = "Itineraries", modifier = Modifier.padding(bottom = 16.dp))
                }
            }

            if (tripId == null) {
                if (itineraries.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillParentMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = stringResource(R.string.no_trips_found), style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                } else {
                    val itinerariesByTrip = trips.filter { it.itineraries.isNotEmpty() }.associateWith { it.itineraries }
                    itinerariesByTrip.forEach { (trip, tripItineraries) ->
                        items(tripItineraries) { itinerary ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("🏷️ ${itinerary.name}", style = MaterialTheme.typography.titleSmall)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("📍 Location: ${itinerary.location}", style = MaterialTheme.typography.bodyMedium)
                                    Text("📅 Date: ${itinerary.startDate}", style = MaterialTheme.typography.bodySmall)

                                    // Mostrar imágenes si hay
                                    val images = processedImagesMap[itinerary.id] ?: itinerary.images ?: emptyList()
                                    if (images.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        LazyRow(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(70.dp)
                                        ) {
                                            items(images) { image ->
                                                ImageThumbnail(
                                                    imagePath = image.imagePath,
                                                    onClick = { selectedImage = Pair(itinerary.id, image.imagePath) }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                    return@LazyColumn
                }
            }

            items(itineraries) { itinerary ->
                var isChecked by remember { mutableStateOf(selectedItineraries.contains(itinerary)) }

                // Launcher para seleccionar imágenes para este itinerario específico
                val imageLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.GetMultipleContents()
                ) { uris: List<Uri> ->
                    // Guardar las URIs de las imágenes seleccionadas
                    val currentUris = itineraryImagesMap[itinerary.id] ?: emptyList()
                    itineraryImagesMap = itineraryImagesMap + (itinerary.id to (currentUris + uris))

                    // Procesar las imágenes y guardarlas localmente
                    val newImages = mutableListOf<ItineraryImage>()
                    uris.forEach { uri ->
                        val localPath = ImageUtils.saveImageToAppStorage(context, uri)
                        if (localPath != null) {
                            val newImage = ItineraryImage(
                                id = UUID.randomUUID().toString(),
                                itineraryId = itinerary.id,
                                imagePath = localPath
                            )
                            newImages.add(newImage)
                        }
                    }

                    // Actualizar el mapa de imágenes procesadas
                    val currentImages = processedImagesMap[itinerary.id] ?: emptyList()
                    processedImagesMap = processedImagesMap + (itinerary.id to (currentImages + newImages))
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = {
                                    isChecked = it
                                    itineraryViewModel.toggleItinerarySelection(itinerary)
                                }
                            )
                            Text(itinerary.name, style = MaterialTheme.typography.titleSmall)
                        }

                        if (isChecked) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("📅 Select date for this itinerary", style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(4.dp))
                            DatePicker(
                                currentDate = itineraryDates[itinerary.id] ?: "",
                                onDateSelected = { date ->
                                    if (isDateInFuture(date)) {
                                        itineraryDates = itineraryDates + (itinerary.id to date)
                                        itineraryDatesValid = itineraryDatesValid + (itinerary.id to true)
                                    } else {
                                        itineraryDatesValid = itineraryDatesValid + (itinerary.id to false)
                                    }
                                },
                                tripStartDate = tripStartDate,
                                tripEndDate = tripEndDate
                            )

                            // Sección de imágenes para el itinerario
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "📸 ${stringResource(R.string.itinerary_images)}",
                                    style = MaterialTheme.typography.titleSmall
                                )

                                // Botón para agregar imágenes a este itinerario
                                Button(
                                    onClick = { imageLauncher.launch("image/*") },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                ) {
                                    Icon(
                                        Icons.Default.AddPhotoAlternate,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(stringResource(R.string.add_images))
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Mostrar imágenes del itinerario
                            val existingImages = itinerary.images ?: emptyList()
                            val newImages = processedImagesMap[itinerary.id] ?: emptyList()
                            val allImages = if (newImages.isNotEmpty()) newImages else existingImages

                            if (allImages.isNotEmpty()) {
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(70.dp)
                                        .padding(vertical = 4.dp)
                                ) {
                                    items(allImages) { image ->
                                        ImageThumbnail(
                                            imagePath = image.imagePath,
                                            onClick = { selectedImage = Pair(itinerary.id, image.imagePath) }
                                        )
                                    }
                                }
                            } else {
                                // Mostrar mensaje si no hay imágenes
                                ImagePlaceholder()
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🛫 Start Date", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        DatePicker(
                            currentDate = tripStartDate,
                            onDateSelected = {
                                tripStartDate = it
                                isTripStartDateValid = isDateInFuture(it)
                            },
                            tripStartDate = tripStartDate,
                            tripEndDate = tripEndDate
                        )
                    }
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🛬 End Date", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        DatePicker(
                            currentDate = tripEndDate,
                            onDateSelected = {
                                tripEndDate = it
                                isTripEndDateValid = isDateInFuture(it)
                            },
                            tripStartDate = tripStartDate,
                            tripEndDate = tripEndDate
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        var isValid = true
                        val tripStart = stringToDate(tripStartDate)
                        val tripEnd = stringToDate(tripEndDate)

                        if (tripStart != null && tripEnd != null && tripStart <= tripEnd && isTripStartDateValid && isTripEndDateValid) {
                            selectedItineraries.forEach { itinerary ->
                                val date = itineraryDates[itinerary.id] ?: ""
                                val isValidDate = itineraryDatesValid[itinerary.id] == true &&
                                        isDateInFuture(date) &&
                                        isDateInTripRange(date, tripStartDate, tripEndDate)

                                if (isValidDate) {
                                    itineraryViewModel.updateItineraryDates(itinerary.id, date, date)
                                } else {
                                    isValid = false
                                    Log.e("ItineraryScreen", "Itinerary ${itinerary.name} has an invalid date.")
                                }
                            }
                        }


                        if (isValid) {
                            val newTripId = UUID.randomUUID().toString()

                            // Crear nueva lista de itinerarios con sus imágenes
                            val newItineraries = selectedItineraries.map { itinerary ->
                                // Obtener las imágenes procesadas para este itinerario
                                val itineraryImages = processedImagesMap[itinerary.id] ?: itinerary.images ?: emptyList()

                                ItineraryItem(
                                    id = UUID.randomUUID().toString(),
                                    name = itinerary.name,
                                    location = itinerary.location,
                                    startDate = itineraryDates[itinerary.id] ?: "",
                                    endDate = itineraryDates[itinerary.id] ?: "",
                                    trip = newTripId,
                                    // Incluir las imágenes del itinerario
                                    images = itineraryImages
                                )
                            }

                            val newTrip = Trip(
                                map = null,
                                id = newTripId,
                                destination = trip?.destination ?: "User Trip",
                                user = currentUser,
                                startDate = tripStartDate,
                                endDate = tripEndDate,
                                itineraries = newItineraries,
                                images = null, // Este campo lo dejamos como null porque ahora cada itinerario tiene sus propias imágenes
                                aiRecommendations = null,
                                imageURL = trip?.imageURL ?: "https://example.com/default-trip-image.jpg"
                            )

                            tripViewModel.addTrip(newTrip)
                            navController.navigate("tripsScreen")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    Text(text = stringResource(R.string.save))
                }
            }
        }
    }
}