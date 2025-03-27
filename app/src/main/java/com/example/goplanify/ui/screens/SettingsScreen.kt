package com.example.goplanify.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.goplanify.R
import com.example.goplanify.ui.viewmodel.SettingsViewModel
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.goplanify.domain.model.User
import java.util.Locale
import androidx.core.content.edit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val settingsState by settingsViewModel.settingsState.collectAsState()
    val context = LocalContext.current

    val user = User(
        userId = "testUser123",
        email = "test@test.com",
        password = "defaultPass",
        firstName = "Test",
        lastName = "User",
        trips = emptyList(),
        imageURL = "https://example.com/user-avatar.png"
    )

    // Cargar y aplicar el idioma
    LaunchedEffect(Unit) {
        val language = settingsViewModel.getSavedLanguage(context)
        setLocale(context, language)
        settingsViewModel.loadPreferences(user)
    }

    Scaffold(
        topBar = {
            CommonTopBar(
                title = stringResource(R.string.settingsScreen),
                navController = navController
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "Settings", style = MaterialTheme.typography.headlineSmall)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Enable Notifications")
                Switch(
                    checked = settingsState.notificationsEnabled,
                    onCheckedChange = { settingsViewModel.toggleNotifications(user, it) }
                )
            }

            LanguageSelector(
                context = context,
                onLanguageChange = { selectedLanguage ->
                    // Cambiar el idioma solo cuando se selecciona manualmente
                    settingsViewModel.changeLanguage(user, selectedLanguage, context)

                    // Volver a la pantalla anterior y luego navegar de nuevo para aplicar el cambio de idioma
                    navController.popBackStack() // Vuelve a la pantalla anterior
                    navController.navigate("settings") // Vuelve a navegar a la pantalla de configuraciones
                }
            )
        }
    }
}




@Composable
fun LanguageSelector(context: Context, onLanguageChange: (String) -> Unit) {
    val languages = listOf("🇬🇧 English", "🇪🇸 Español", "🇫🇷 Français", "🇵🇹 Português")
    val languageCodes = listOf("en", "es", "fr", "pt")

    // Obtener el idioma actual desde SharedPreferences (al iniciar la pantalla)
    val savedLanguage = getSavedLanguage(context)
    var selectedLanguage by remember { mutableStateOf(savedLanguage) }

    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(text = "Select Language:")
        Button(onClick = { expanded = true }) {
            Text(selectedLanguage.uppercase())
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            languages.forEachIndexed { index, language ->
                DropdownMenuItem(
                    text = { Text(language) },
                    onClick = {
                        selectedLanguage = languageCodes[index]
                        onLanguageChange(languageCodes[index])
                        expanded = false
                    }
                )
            }
        }
    }
}

fun getSavedLanguage(context: Context): String {
    val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    return sharedPreferences.getString("language", Locale.getDefault().language) ?: "en"
}

fun setLocale(context: Context, languageCode: String) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)

    val config = context.resources.configuration
    config.setLocale(locale)

    context.resources.updateConfiguration(config, context.resources.displayMetrics)

    val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    sharedPreferences.edit() { putString("language", languageCode) }
}

