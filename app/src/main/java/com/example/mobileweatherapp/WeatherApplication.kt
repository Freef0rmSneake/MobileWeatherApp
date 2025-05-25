package com.example.mobileweatherapp

import android.app.Application
import com.example.mobileweatherapp.database.WeatherDatabase

class WeatherApplication : Application() {
    val database: WeatherDatabase by lazy { WeatherDatabase.getDatabase(this) }
} 