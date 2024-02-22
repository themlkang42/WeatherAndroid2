package com.mlkang.codingexercise.data

import com.mlkang.codingexercise.model.Location
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingRemoteDataSource {
    @GET("geo/1.0/direct?limit=10&appid=${WeatherRemoteDataSource.API_KEY}")
    suspend fun locations(@Query("q") query: String): List<Location>
}