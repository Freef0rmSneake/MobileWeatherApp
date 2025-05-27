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

internal class WeatherRepository(
    private val weatherDao: WeatherDao,
    private val apiClient: HttpClient
) {
    companion object {
        private const val TAG = "WeatherRepository"
    }

    suspend fun getWeatherForCity(city: String): Weather {
        return try {
            val apiResponse = fetchFromApi(city)
            val weather = Weather(
                cityName = apiResponse.name,
                temperature = apiResponse.main.temp,
                description = apiResponse.weather.firstOrNull()?.description ?: "",
                icon = apiResponse.weather.firstOrNull()?.icon ?: ""
            )
            saveToCache(weather)
            weather
        } catch (e: Exception) {
            Log.e(TAG, "Błąd API: ${e.message}")
            val cached = weatherDao.getWeatherForCity(city)
            if (cached != null) {
                Log.d(TAG, "Używam danych z cache dla miasta $city")
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
            Log.e(TAG, "Błąd zapisu do cache: ${e.message}")
        }
    }

    private suspend fun fetchFromApi(city: String): WeatherApiResponse {
        return apiClient.get("https://api.openweathermap.org/data/2.5/weather") {
            parameter("q", city)
            parameter("units", "metric")
            parameter("appid", BuildConfig.OW_KEY)
            parameter("lang", "pl")
            contentType(ContentType.Application.Json)
        }.body()
    }

    suspend fun clearOldCache(maxAgeMs: Long = 24 * 60 * 60 * 1000) {
        val threshold = System.currentTimeMillis() - maxAgeMs
        weatherDao.deleteOldCache(threshold)
    }
} 