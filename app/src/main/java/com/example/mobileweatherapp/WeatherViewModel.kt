package com.example.mobileweatherapp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {
    private val api = WeatherApiService.create()

    private val _weather = MutableStateFlow<WeatherResponse?>(null)
    val weather: StateFlow<WeatherResponse?> = _weather

    init {
        fetchWeather()
    }

    private fun fetchWeather() {
        viewModelScope.launch {
            try {
                val response = api.getWeather("Wroclaw", BuildConfig.OW_KEY)
                _weather.value = response
            } catch (e: Exception) {
                Log.e("WeatherViewModel", "Błąd pobierania pogody: ${e.message}")
            }
        }
    }
}

