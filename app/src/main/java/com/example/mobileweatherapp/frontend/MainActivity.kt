package com.example.mobileweatherapp.frontend

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.example.mobileweatherapp.backend.WeatherViewModel
import compose.icons.FeatherIcons
import compose.icons.feathericons.Cloud
import compose.icons.feathericons.Droplet
import compose.icons.feathericons.Wind
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import coil.compose.rememberAsyncImagePainter
import com.example.mobileweatherapp.database.ForecastData
import com.example.mobileweatherapp.database.WeatherData

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

    @RequiresApi(Build.VERSION_CODES.O)
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeatherScreen(viewModel: WeatherViewModel) {
    val weather by viewModel.weather.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState(initial = false)
    val error by viewModel.error.observeAsState()
    var selectedCity by remember { mutableStateOf(polskieMiasta[0]) }
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1E88E5),
                        Color(0xFF0D47A1)
                    )
                )
            )
    )

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
            Text(
                text = "Wybrane miasto: $selectedCity"
            )
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
fun WeatherDetailItem(icon: ImageVector, value: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 12.sp
        )
    }
}

data class Temp(
    val min: Double,
    val max: Double
)

data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ForecastItem(forecast: ForecastData) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = LocalDateTime
                    .ofEpochSecond(forecast.dt, 0, java.time.ZoneOffset.UTC)
                    .format(DateTimeFormatter.ofPattern("EEEE HH:mm")),
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = "https://openweathermap.org/img/wn/${forecast.icon}.png"
                    ),
                    contentDescription = "Weather icon",
                    modifier = Modifier.size(30.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "${forecast.tempMin.toInt()}° / ${forecast.tempMax.toInt()}°",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
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
            text = weather.cityName,
            color = Color.White,
            style = MaterialTheme.typography.titleLarge,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEEE, d MMM")),
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = "https://openweathermap.org/img/wn/${weather.icon}@4x.png"
                ),
                contentDescription = "Weather icon",
                modifier = Modifier.size(120.dp),
                contentScale = ContentScale.Fit
            )

            Column {
                Text(
                    text = "${weather.temperature}°C",
                    color = Color.White,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = weather.description.replaceFirstChar { it.titlecase() },
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(25.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Spacer(modifier = Modifier.height(50.dp))
            Text(
                text = "Obecne warunki pogodowe:",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(50.dp))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            WeatherDetailItem(
                icon = FeatherIcons.Droplet,
                value = "${weather.humidity}%",
                label = "Wilgotność"
            )
            WeatherDetailItem(
                icon = FeatherIcons.Wind,
                value = "${weather.windSpeed} km/h",
                label = "Wiatr"
            )
            WeatherDetailItem(
                icon = FeatherIcons.Cloud,
                value = "${weather.clouds}%",
                label = "Zachmurzenie"
            )
        }
        Spacer(modifier = Modifier.height(25.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                Text(
                    text = "Prognoza pogody:",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            weather.forecasts.take(40).forEach { forecast ->
                ForecastItem(forecast)
                Spacer(modifier = Modifier.height(8.dp))}
        }
    }
}