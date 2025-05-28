package com.example.mobileweatherapp.database

import androidx.room.*

@Dao
internal interface WeatherDao {
    @Query("SELECT * FROM weather_cache WHERE cityName = :city")
    suspend fun getWeatherForCity(city: String): WeatherData?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: WeatherData)

    @Query("DELETE FROM weather_cache WHERE cityName = :city")
    suspend fun deleteWeatherForCity(city: String)

    @Query("DELETE FROM weather_cache WHERE timestamp < :timestamp")
    suspend fun deleteOldCache(timestamp: Long)
} 