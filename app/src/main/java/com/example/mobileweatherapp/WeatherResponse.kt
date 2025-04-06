package com.example.mobileweatherapp

// dane z OpenWeather API
data class WeatherResponse(
    val name: String,   // miasto
    val main: Main,     // info pogodowe (temp)
    val weather: List<Weather>  // lista warunków (opis, ikonka)
)

// dane o temperaturze
data class Main(
    val temp: Float
)


// dane o stanie pogody
data class Weather(
    val description: String,
    val icon: String
)
