package com.example.mobileweatherapp.frontend

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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.example.mobileweatherapp.backend.WeatherViewModel
import com.example.mobileweatherapp.frontend.model.WeatherData


val polskieMiasta = listOf(
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


class MainActivity : ComponentActivity() {
    private val viewModel: WeatherViewModel by viewModels { 
        ViewModelProvider.AndroidViewModelFactory(application) 
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                WeatherScreen(viewModel)
            }
        }

        viewModel.getWeatherData("Warszawa")
    }
}

@Composable
fun WeatherScreen(viewModel: WeatherViewModel) {
    val weather by viewModel.weather.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState(initial = false)
    val error by viewModel.error.observeAsState()
    var selectedCity by remember { mutableStateOf(polskieMiasta[0]) }
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { showDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(text = "Wybrane miasto: $selectedCity")
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(text = "Wybierz miasto") },
                text = {
                    Column {
                        polskieMiasta.forEach { city ->
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
                        Text(text = "Anuluj")
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        when {
            isLoading -> {
                CircularProgressIndicator()
            }
            error != null -> {
                Text(
                    text = "Wystąpił błąd: ${error ?: ""}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
            weather != null -> {
                WeatherContent(weather = weather!!)
            }
        }
    }
}

@Composable
fun WeatherContent(weather: WeatherData) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Miasto: ${weather.cityName}",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "Temperatura: ${weather.temperature}°C")
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Opis: ${weather.description}")
    }
} 