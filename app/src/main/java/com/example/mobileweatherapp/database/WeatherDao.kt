package com.example.mobileweatherapp.database

import androidx.room.*

@Dao
interface WeatherDao {
    @Query("SELECT * FROM weather_cache WHERE cityName = :city")
    suspend fun getWeatherForCity(city: String): Weather?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: Weather)

    @Query("DELETE FROM weather_cache WHERE cityName = :city")
    suspend fun deleteWeatherForCity(city: String)

    @Query("DELETE FROM weather_cache WHERE timestamp < :timestamp")
    suspend fun deleteOldCache(timestamp: Long)
} 