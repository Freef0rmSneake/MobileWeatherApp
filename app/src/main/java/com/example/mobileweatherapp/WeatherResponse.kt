package com.example.mobileweatherapp

// Klasa reprezentująca odpowiedź z OpenWeatherAPI
data class WeatherResponse(
    val name: String,           // Nazwa miasta
    val main: Main,             // Informacje pogodowe (np. temperatura)
    val weather: List<Weather>  // Lista warunków pogodowych (opis, ikona)
)

// Klasa reprezentująca dane o temperaturze
data class Main(
    val temp: Float             // Temperatura w °C
)

// Klasa reprezentująca opis warunków pogodowych (np. "słonecznie")
data class Weather(
    val description: String,    // Opis (np. "pochmurno")
    val icon: String            // Ikona (np. "01d" – słońce)
)
