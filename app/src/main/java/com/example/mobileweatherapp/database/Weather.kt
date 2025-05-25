package com.example.mobileweatherapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_cache")
internal data class Weather(
    @PrimaryKey
    val cityName: String,
    val temperature: Float,
    val description: String,
    val icon: String,
    val timestamp: Long = System.currentTimeMillis()
) 