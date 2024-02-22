package com.mlkang.codingexercise.data

import Weatherpb.WeatherPb
import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.mlkang.codingexercise.model.Weather
import kotlinx.coroutines.flow.first
import java.io.InputStream
import java.io.OutputStream

val Context.weatherStore: DataStore<WeatherPb> by dataStore(
    fileName = "weather.pb",
    serializer = WeatherSerializer
)

suspend fun DataStore<WeatherPb>.updateWeather(weather: Weather) {
    updateData { weather.toWeatherPb() }
}

suspend fun DataStore<WeatherPb>.getStoredWeather(): Weather? {
    return try {
        val weatherPb = data.first()
        Weather(weatherPb)
    } catch (exception: NoSuchElementException) {
        null
    }
}

object WeatherSerializer : Serializer<WeatherPb> {
    override val defaultValue: WeatherPb = WeatherPb.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): WeatherPb {
        try {
            return WeatherPb.parseFrom(input)
        } catch (exception: Exception) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: WeatherPb, output: OutputStream) = t.writeTo(output)
}