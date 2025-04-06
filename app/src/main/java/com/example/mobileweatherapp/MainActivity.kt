package com.example.mobileweatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// główne
class MainActivity : ComponentActivity() {
    private val viewModel: WeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                WeatherScreen(viewModel)
            }
        }
    }
}

// Composable wyświetlający ekran pogody
@Composable
fun WeatherScreen(viewModel: WeatherViewModel) {
    val weather by viewModel.weather.collectAsState()

    if (weather == null) {
        // Jeśli dane się jeszcze ładują
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        // Główna zawartość z danymi pogodowymi
        WeatherContent(weather = weather!!)
    }
}

// UI pokazujący dane pogodowe
@Composable
fun WeatherContent(weather: WeatherResponse) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Miasto: ${weather.name}", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(12.dp))
        Text("Temperatura: ${weather.main.temp}°C")
        Spacer(modifier = Modifier.height(8.dp))
        Text("Opis: ${weather.weather.firstOrNull()?.description ?: "-"}")
    }
}

// Podgląd do edycji UI
@Preview(showBackground = true)
@Composable
fun PreviewWeatherScreen() {
    val sampleWeather = WeatherResponse(
        name = "Wrocław",
        main = Main(temp = 22.5f),
        weather = listOf(Weather(description = "słonecznie", icon = "01d"))
    )
    MaterialTheme {
        WeatherContent(weather = sampleWeather)
    }
}