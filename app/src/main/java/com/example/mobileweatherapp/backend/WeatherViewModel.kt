package com.example.mobileweatherapp.backend

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mobileweatherapp.WeatherApplication
import com.example.mobileweatherapp.database.WeatherData
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.gson.*
import kotlinx.coroutines.launch

class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    private val _weather = MutableLiveData<WeatherData?>()
    val weather: LiveData<WeatherData?> = _weather

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            gson()
        }
    }

    private val repository: WeatherRepository

    init {
        val app = getApplication<WeatherApplication>()
        val weatherDao = app.database.weatherDao()
        repository = WeatherRepository(weatherDao, client)
        
        viewModelScope.launch {
            repository.clearOldCache()
        }
    }

    fun getWeatherData(city: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                val weatherData = repository.getWeatherForCity(city)
                _weather.value = weatherData
            } catch (e: Exception) {
                _error.value = when {
                    e.message?.contains("Unable to resolve host") == true -> 
                        "Brak połączenia z internetem"
                    e.message?.contains("404") == true -> 
                        "Nie znaleziono danych dla miasta $city"
                    e.message?.contains("401") == true -> 
                        "Błąd autoryzacji API"
                    else -> "Wystąpił nieoczekiwany błąd: ${e.message}"
                }
                _weather.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        client.close()
    }
} 