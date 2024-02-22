package com.mlkang.codingexercise.data

import com.mlkang.codingexercise.model.Weather
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherRemoteDataSource {
    @GET("data/2.5/weather?units=imperial&appid=${API_KEY}")
    suspend fun weather(@Query("lat") lat: Double, @Query("lon") lon: Double): Weather

    companion object {
        const val ROOT_URL = "https://api.openweathermap.org"
        const val API_KEY = "ad7ca150620ec70ca2a0b582c6a69ba3"
    }
}