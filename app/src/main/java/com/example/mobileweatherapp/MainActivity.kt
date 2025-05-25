package com.example.mobileweatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// Lista 10 największych miast w Polsce
val polishCities = listOf(
    "Warszawa",
    "Kraków",
    "Łódź",
    "Wrocław",
    "Poznań",
    "Gdańsk",
    "Szczecin",
    "Bydgoszcz",
    "Lublin",
    "Katowice"
)

// Główna aktywność aplikacji
class MainActivity : ComponentActivity() {
    // ViewModel do komunikacji z danymi
    private val viewModel: WeatherViewModel by viewModels()

    // Funkcja onCreate jest wywoływana podczas tworzenia aktywności
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Wygląd aplikacji przy użyciu Jetpack Compose
        setContent {
            MaterialTheme {
                WeatherScreen(viewModel) // Pokazujemy ekran pogodowy
            }
        }

        // Domyślnie pokazujemy pogodę dla Warszawy
        viewModel.getWeatherData("Warszawa")
    }
}

// Komponent, który wyświetla dane pogodowe
@Composable
fun WeatherScreen(viewModel: WeatherViewModel) {
    val weather by viewModel.weather.observeAsState()
    var selectedCity by remember { mutableStateOf(polishCities[0]) }
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // City selector button
        Button(
            onClick = { showDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(text = selectedCity)
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Wybierz miasto") },
                text = {
                    Column {
                        polishCities.forEach { city ->
                            Text(
                                text = city,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedCity = city
                                        showDialog = false
                                        viewModel.getWeatherData(city)
                                    }
                                    .padding(vertical = 12.dp, horizontal = 16.dp)
                            )
                        }
                    }
                },
                confirmButton = { },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Anuluj")
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Wyświetlanie danych pogodowych
        if (weather == null) {
            CircularProgressIndicator()
        } else {
            WeatherContent(weather = weather!!)
        }
    }
}

// Komponent, który wyświetla szczegóły pogodowe
@Composable
fun WeatherContent(weather: WeatherResponse) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Nazwa miasta
        Text("Miasto: ${weather.name}", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(12.dp)) // Odstęp między elementami
        // Temperatura
        Text("Temperatura: ${weather.main.temp}°C")
        Spacer(modifier = Modifier.height(8.dp)) // Odstęp
        // Opis pogody
        Text("Opis: ${weather.weather.firstOrNull()?.description ?: "-"}")
    }
}