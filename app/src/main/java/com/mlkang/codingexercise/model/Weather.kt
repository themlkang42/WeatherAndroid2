package com.mlkang.codingexercise.model

import Weatherpb.CloudsPb
import Weatherpb.WeatherConditionPb
import Weatherpb.WeatherCoordPb
import Weatherpb.WeatherMainPb
import Weatherpb.WeatherPb
import Weatherpb.WindPb
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Weather(
    @Json(name = "coord") val coord: WeatherCoord,
    @Json(name = "weather") val conditions: List<WeatherCondition>,
    @Json(name = "main") val main: WeatherMain,
    @Json(name = "visibility") val visibility: Int,
    @Json(name = "wind") val wind: Wind,
    @Json(name = "clouds") val clouds: Clouds,
    @Json(name = "name") @Deprecated("field is deprecated in the API") val name: String
) {
    constructor(weatherPb: WeatherPb) : this(
        coord = WeatherCoord(weatherPb.coord),
        conditions = weatherPb.conditionsList.map { WeatherCondition(it) },
        main = WeatherMain(weatherPb.main),
        visibility = weatherPb.visibility,
        wind = Wind(weatherPb.wind),
        clouds = Clouds(weatherPb.clouds),
        name = weatherPb.name
    )

    fun toWeatherPb(): WeatherPb {
        return WeatherPb.newBuilder()
            .setCoord(coord.toWeatherCoordPb())
            .addAllConditions(conditions.map { it.toWeatherConditionPb() })
            .setMain(main.toWeatherMainPb())
            .setVisibility(visibility)
            .setWind(wind.toWindPb())
            .setClouds(clouds.toCloudsPb())
            .setName(name)
            .build()
    }
}

@JsonClass(generateAdapter = true)
data class WeatherCoord(
    @Json(name = "lon") val lon: Double,
    @Json(name = "lat") val lat: Double
) {
    constructor(weatherCoordPb: WeatherCoordPb) : this(
        lon = weatherCoordPb.lon,
        lat = weatherCoordPb.lat
    )

    fun toWeatherCoordPb(): WeatherCoordPb {
        return WeatherCoordPb.newBuilder()
            .setLon(lon)
            .setLat(lat)
            .build()
    }
}

@JsonClass(generateAdapter = true)
data class WeatherCondition(
    @Json(name = "id") val id: Int,
    @Json(name = "main") val main: String,
    @Json(name = "description") val description: String,
    @Json(name = "icon") val icon: String
) {
    constructor(weatherConditionPb: WeatherConditionPb) : this(
        id = weatherConditionPb.id,
        main = weatherConditionPb.main,
        description = weatherConditionPb.description,
        icon = weatherConditionPb.icon
    )

    fun toWeatherConditionPb(): WeatherConditionPb {
        return WeatherConditionPb.newBuilder()
            .setId(id)
            .setMain(main)
            .setDescription(description)
            .setIcon(icon)
            .build()
    }
}

@JsonClass(generateAdapter = true)
data class WeatherMain(
    @Json(name = "temp") val temp: Double,
    @Json(name = "feels_like") val feelsLike: Double,
    @Json(name = "temp_min") val tempMin: Double,
    @Json(name = "temp_max") val tempMax: Double,
    @Json(name = "pressure") val pressure: Int,
    @Json(name = "humidity") val humidity: Int
) {
    constructor(weatherMainPb: WeatherMainPb) : this(
        temp = weatherMainPb.temp,
        feelsLike = weatherMainPb.feelsLike,
        tempMin = weatherMainPb.tempMin,
        tempMax = weatherMainPb.tempMax,
        pressure = weatherMainPb.pressure,
        humidity = weatherMainPb.humidity
    )

    fun toWeatherMainPb(): WeatherMainPb {
        return WeatherMainPb.newBuilder()
            .setTemp(temp)
            .setFeelsLike(feelsLike)
            .setTempMin(tempMin)
            .setTempMax(tempMax)
            .setPressure(pressure)
            .setHumidity(humidity)
            .build()
    }
}

@JsonClass(generateAdapter = true)
data class Wind(
    @Json(name = "speed") val speed: Double,
    @Json(name = "deg") val deg: Int
) {
    constructor(windPb: WindPb) : this(
        speed = windPb.speed,
        deg = windPb.deg
    )

    fun toWindPb(): WindPb {
        return WindPb.newBuilder()
            .setSpeed(speed)
            .setDeg(deg)
            .build()
    }
}

@JsonClass(generateAdapter = true)
data class Clouds(
    @Json(name = "all") val all: Int
) {
    constructor(cloudsPb: CloudsPb) : this(
        all = cloudsPb.all
    )

    fun toCloudsPb(): CloudsPb {
        return CloudsPb.newBuilder()
            .setAll(all)
            .build()
    }
}