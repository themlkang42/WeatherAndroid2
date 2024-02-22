package com.mlkang.codingexercise.data

import Weatherpb.WeatherPb
import android.content.Context
import androidx.datastore.core.DataStore
import com.mlkang.codingexercise.model.Weather
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val weatherRemoteDataSource: WeatherRemoteDataSource,
    @ApplicationContext private val context: Context
) {
    private val weatherStore: DataStore<WeatherPb> = context.weatherStore

    suspend fun getWeather(lat: Double, lon: Double): Weather {
        val weather = weatherRemoteDataSource.weather(lat, lon)
        weatherStore.updateWeather(weather)
        return weather
    }

    suspend fun getStoredWeather(): Weather? {
        return weatherStore.getStoredWeather()
    }
}