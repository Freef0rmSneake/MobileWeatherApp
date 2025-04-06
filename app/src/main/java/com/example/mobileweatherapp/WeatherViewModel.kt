package com.example.mobileweatherapp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.gson.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {

    private val _weather = MutableStateFlow<WeatherResponse?>(null)
    val weather: StateFlow<WeatherResponse?> = _weather

    // Inicjalizacja Ktor Clienta z pluginem do JSONa
    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            gson()
        }
    }

    init {
        getWeatherData("Wroclaw")
    }

    private fun getWeatherData(city: String) {
        viewModelScope.launch {
            try {
                val response: WeatherResponse = client.get("https://api.openweathermap.org/data/2.5/weather") {
                    parameter("q", city)
                    parameter("units", "metric")
                    parameter("appid", BuildConfig.OW_KEY)
                    contentType(ContentType.Application.Json)
                }.body()

                _weather.value = response
            } catch (e: Exception) {
                Log.e("WeatherViewModel", "Błąd Ktor: ${e.message}")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        client.close() // zamknij klienta przy zamykaniu ViewModelu
    }
}
