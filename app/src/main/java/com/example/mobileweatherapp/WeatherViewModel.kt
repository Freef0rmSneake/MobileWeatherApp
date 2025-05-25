package com.example.mobileweatherapp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.gson.*
import kotlinx.coroutines.launch

// ViewModel do zarządzania stanem aplikacji i pobierania danych pogodowych
class WeatherViewModel : ViewModel() {

    // LiveData przechowujące dane o pogodzie, które mogą być obserwowane w UI
    private val _weather = MutableLiveData<WeatherResponse?>()
    val weather: LiveData<WeatherResponse?> = _weather

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // Inicjalizacja Ktor Clienta, który będzie używany do zapytań HTTP
    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            gson()  // Używamy GSON do serializacji i deserializacji JSON
        }
    }

    // Funkcja odpowiedzialna za pobieranie danych o pogodzie
    fun getWeatherData(city: String) {
        // Używamy viewModelScope, aby uruchomić zapytanie w tle
        viewModelScope.launch {
            try {
                _isLoading.postValue(true)
                _error.postValue(null)
                
                // Wysyłamy zapytanie do OpenWeather API
                val response: WeatherResponse = client.get("https://api.openweathermap.org/data/2.5/weather") {
                    parameter("q", city)
                    parameter("units", "metric")  // Celsjusz
                    parameter("appid", BuildConfig.OW_KEY)  // Użycie klucza API z BuildConfig
                    contentType(ContentType.Application.Json)
                }.body() // Otrzymujemy odpowiedź w postaci WeatherResponse

                // Zapisujemy dane do LiveData
                _weather.postValue(response)
            } catch (e: Exception) {
                Log.e("WeatherViewModel", "Błąd: ${e.message}")
                _error.postValue("Nie udało się pobrać danych dla miasta: $city")
                _weather.postValue(null)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    // Funkcja wywoływana, gdy ViewModel jest niszczony, zamykamy wtedy klienta HTTP
    override fun onCleared() {
        super.onCleared()
        client.close()  // Zamknięcie klienta
    }
}
