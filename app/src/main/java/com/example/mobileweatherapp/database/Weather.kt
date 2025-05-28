package com.example.mobileweatherapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class ForecastData(
    val dt: Long,
    val tempMin: Float,
    val tempMax: Float,
    val icon: String
)

class Converters {
    private val gson = Gson()
    private val type = object : TypeToken<List<ForecastData>>() {}.type

    @TypeConverter
    fun fromForecastData(value: String?): List<ForecastData>? {
        return value?.let { gson.fromJson(it, type) }
    }

    @TypeConverter
    fun toForecastData(list: List<ForecastData>?): String? {
        return list?.let { gson.toJson(it, type) }
    }
}

@Entity(tableName = "weather_cache")
data class WeatherData(
    @PrimaryKey
    val cityName: String,
    val temperature: Float,
    val description: String,
    val icon: String,
    val humidity: Int,
    val windSpeed: Float,
    val clouds: Int,
    val forecasts: List<ForecastData>,
    val timestamp: Long = System.currentTimeMillis()
) 