package com.example.mobileweatherapp.backend

import android.util.Log
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import com.example.mobileweatherapp.BuildConfig
import com.example.mobileweatherapp.database.Weather
import com.example.mobileweatherapp.database.WeatherDao
import com.google.gson.annotations.SerializedName

private data class WeatherApiResponse(
    @SerializedName("name")
    val name: String,
    @SerializedName("main")
    val main: Main,
    @SerializedName("weather")
    val weather: List<WeatherDescription>
)

private data class Main(
    @SerializedName("temp")
    val temp: Float
)

private data class WeatherDescription(
    @SerializedName("description")
    val description: String,
    @SerializedName("icon")
    val icon: String
)

class WeatherRepository(
    private val weatherDao: WeatherDao,
    private val apiClient: HttpClient
) {
    suspend fun getWeatherForCity(city: String): Weather {
        return try {
            // Próba pobrania świeżych danych z API
            val apiResponse = fetchFromApi(city)
            val weather = Weather(
                cityName = apiResponse.name,
                temperature = apiResponse.main.temp,
                description = apiResponse.weather.firstOrNull()?.description ?: "",
                icon = apiResponse.weather.firstOrNull()?.icon ?: ""
            )
            // Zapisanie odpowiedzi w cache
            saveToCache(weather)
            weather
        } catch (e: Exception) {
            Log.e("WeatherRepository", "Błąd API: ${e.message}")
            // W przypadku błędu, próba pobrania danych z cache
            val cached = weatherDao.getWeatherForCity(city)
            if (cached != null) {
                Log.d("WeatherRepository", "Używam danych z cache dla miasta $city")
                cached
            } else {
                throw e
            }
        }
    }

    private suspend fun saveToCache(weather: Weather) {
        try {
            weatherDao.insertWeather(weather)
        } catch (e: Exception) {
            Log.e("WeatherRepository", "Błąd zapisu do cache: ${e.message}")
        }
    }

    private suspend fun fetchFromApi(city: String): WeatherApiResponse {
        return apiClient.get("https://api.openweathermap.org/data/2.5/weather") {
            parameter("q", city)
            parameter("units", "metric")
            parameter("appid", BuildConfig.OW_KEY)
            parameter("lang", "pl") // Dodanie parametru języka polskiego
            contentType(ContentType.Application.Json)
        }.body()
    }

    suspend fun clearOldCache(maxAgeMs: Long = 24 * 60 * 60 * 1000) { // 24 godziny
        val threshold = System.currentTimeMillis() - maxAgeMs
        weatherDao.deleteOldCache(threshold)
    }
} 