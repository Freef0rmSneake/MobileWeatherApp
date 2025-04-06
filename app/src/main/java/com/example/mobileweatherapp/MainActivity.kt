package com.example.mobileweatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

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

        // Funkcja pobierająca dane o pogodzie dla Wrocławia
        viewModel.getWeatherData("Wrocław")
    }
}

// Komponent, który wyświetla dane pogodowe
@Composable
fun WeatherScreen(viewModel: WeatherViewModel) {
    // Dane pogodowe z LiveData ViewModelu pobierają się
    val weather by viewModel.weather.observeAsState()

    // Jeśli dane są jeszcze ładowane (null), wyświetla się kółko ładowania
    if (weather == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator() // kółko ładowania
        }
    } else {
        // Gdy gotowe, dane się wyświetlają
        WeatherContent(weather = weather!!)
    }
}

// Komponent, który wyświetla szczegóły pogodowe
@Composable
fun WeatherContent(weather: WeatherResponse) {
    Column(
        modifier = Modifier
            .fillMaxSize() // Wypełnia całą dostępna przestrzeń
            .padding(24.dp), // Dodajemy odstępy
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

// Przykładowy widok
@Preview(showBackground = true)
@Composable
fun PreviewWeatherScreen() {
    // Przykładowe dane pogodowe
    val sampleWeather = WeatherResponse(
        name = "Wrocław",
        main = Main(temp = 22.5f),
        weather = listOf(Weather(description = "słonecznie", icon = "01d"))
    )
    MaterialTheme {
        WeatherContent(weather = sampleWeather)
    }
}
