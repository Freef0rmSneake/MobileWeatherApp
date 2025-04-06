package com.example.mobileweatherapp

// Główna odpowiedź z OpenWeather API
data class WeatherResponse(
    val name: String,           // Miasto
    val main: Main,             // Informacje pogodowe (temperatura)
    val weather: List<Weather>  // Lista warunków pogodowych (opis, ikona)
)

// Dane o temperaturze i innych parametrach
data class Main(
    val temp: Float             // Temperatura w °C
)

// Dane o stanie pogody
data class Weather(
    val description: String,    // Opis
    val icon: String            // Ikona
)