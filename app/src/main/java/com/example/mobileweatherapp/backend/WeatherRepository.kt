package com.example.mobileweatherapp.backend

import android.util.Log
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import com.example.mobileweatherapp.BuildConfig
import com.example.mobileweatherapp.database.ForecastData
import com.example.mobileweatherapp.database.WeatherDao
import com.example.mobileweatherapp.database.WeatherData
import com.google.gson.annotations.SerializedName

private data class ForecastMain(
    @SerializedName("temp_min")
    val tempMin: Float,
    @SerializedName("temp_max")
    val tempMax: Float
)

private data class Forecast(
    @SerializedName("dt")
    val dt: Long,
    @SerializedName("main")
    val forecastMain: ForecastMain,
    @SerializedName("weather")
    val weather: List<WeatherDescription>
)

private data class ForecastApiResponse(
    @SerializedName("list")
    val forecasts: List<Forecast>
)

private data class WeatherApiResponse(
    @SerializedName("name")
    val name: String,
    @SerializedName("main")
    val main: Main,
    @SerializedName("dt")
    val dt: Long,
    @SerializedName("wind")
    val wind: Wind,
    @SerializedName("clouds")
    val clouds: Clouds,
    @SerializedName("weather")
    val weather: List<WeatherDescription>
)

private data class Wind(
    @SerializedName("speed")
    val speed: Float,
    @SerializedName("degree")
    val degree: Int
)

private data class Clouds(
    @SerializedName("all")
    val all: Int
)

private data class Main(
    @SerializedName("temp")
    val temp: Float,
    @SerializedName("temp_min")
    val tempMin: Float,
    @SerializedName("temp_max")
    val tempMax: Float,
    @SerializedName("humidity")
    val humidity: Int
)

private data class WeatherDescription(
    @SerializedName("id")
    val id: Int,
    @SerializedName("main")
    val main: String,
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

    suspend fun getWeatherForCity(city: String): WeatherData {
        return try {
            val apiResponse = fetchFromApi(city)
            val forecastResponse = fetchForecastFromApi(city)
            val forecastsData = ArrayList<ForecastData>();

            for (forecast in forecastResponse.forecasts) {
                forecastsData.add(
                    ForecastData(
                        dt = forecast.dt,
                        tempMin = forecast.forecastMain.tempMin,
                        tempMax = forecast.forecastMain.tempMax,
                        icon = forecast.weather[0].icon
                    )
                )
            }

            val weather = WeatherData(
                cityName = apiResponse.name,
                temperature = apiResponse.main.temp,
                description = apiResponse.weather.firstOrNull()?.description ?: "",
                icon = apiResponse.weather.firstOrNull()?.icon ?: "",
                humidity = apiResponse.main.humidity,
                windSpeed = apiResponse.wind.speed,
                clouds = apiResponse.clouds.all,
                forecasts = forecastsData
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

    private suspend fun saveToCache(weather: WeatherData) {
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

    private suspend fun fetchForecastFromApi(city: String): ForecastApiResponse {
        return apiClient.get("https://api.openweathermap.org/data/2.5/forecast") {
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