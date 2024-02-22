package com.mlkang.codingexercise.view

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.mlkang.codingexercise.data.GeocodingRemoteDataSource
import com.mlkang.codingexercise.data.WeatherRemoteDataSource
import com.mlkang.codingexercise.data.WeatherRepository
import com.mlkang.codingexercise.data.weatherStore
import com.mlkang.codingexercise.model.Location
import com.mlkang.codingexercise.model.Weather
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val geocodingRemoteDataSource: GeocodingRemoteDataSource,
    private val weatherRepository: WeatherRepository
) : ViewModel() {
    private val _textInputState = MutableStateFlow("")
    val textInputState = _textInputState.asStateFlow()

    private val _locationsState = MutableStateFlow(emptyList<Location>())
    val locationsState = _locationsState.asStateFlow()

    private val _showLocationsState = MutableStateFlow(false)
    val showLocationsState = _showLocationsState.asStateFlow()

    private val _weatherState = MutableStateFlow<Weather?>(null)
    val weatherState = _weatherState.asStateFlow()

    private val clickLocationState = MutableSharedFlow<Location>(extraBufferCapacity = 1)

    init {
        viewModelScope.launch {
            val storedWeather = weatherRepository.getStoredWeather()
            if (storedWeather != null) {
                _weatherState.value = storedWeather
            }
        }

        viewModelScope.launch {
            textInputState
                .debounce(300)
                .mapLatest { text ->
                    if (text.isEmpty()) {
                        emptyList()
                    } else {
                        geocodingRemoteDataSource.locations(text)
                    }
                }
                .catch {
                    Log.e(MainViewModel::class.simpleName, "Error getting locations", it)
                }
                .collect { locations ->
                    _locationsState.value = locations
                    _showLocationsState.value = locations.isNotEmpty()
                }
        }

        viewModelScope.launch {
            clickLocationState
                .debounce(300)
                .mapLatest { location ->
                    weatherRepository.getWeather(location.lat, location.lon)
                }
                .catch {
                    Log.e(MainViewModel::class.simpleName, "Error getting weather", it)
                }
                .collect {
                    _weatherState.value = it
                    _showLocationsState.value = false
                }
        }
    }

    fun updateTextInput(text: String) {
        _textInputState.value = text
    }

    fun clickLocation(location: Location) {
        clickLocationState.tryEmit(location)
    }
}