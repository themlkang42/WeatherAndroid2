package com.mlkang.codingexercise.view

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mlkang.codingexercise.data.GeocodingRemoteDataSource
import com.mlkang.codingexercise.data.WeatherRepository
import com.mlkang.codingexercise.model.Location
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val geocodingRemoteDataSource: GeocodingRemoteDataSource,
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    private val _showLocationsState = MutableStateFlow(false)
    val showLocationsState = _showLocationsState.asStateFlow()

    private val _textInputState = MutableStateFlow("")
    val textInputState = _textInputState.asStateFlow()

    val locationsState = textInputState
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
        .onEach { locations -> _showLocationsState.value = locations.isNotEmpty() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val clickLocationFlow = MutableSharedFlow<Location>(extraBufferCapacity = 1)

    private val weatherFromClicksFlow = clickLocationFlow
        .debounce(300)
        .mapLatest { location ->
            weatherRepository.getWeather(location.lat, location.lon)
        }
        .catch {
            Log.e(MainViewModel::class.simpleName, "Error getting weather", it)
        }
        .onEach { _showLocationsState.value = false }
        .shareIn(viewModelScope, SharingStarted.Eagerly)

    val weatherState = merge(weatherFromClicksFlow, weatherRepository::getStoredWeather.asFlow())
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun updateTextInput(text: String) {
        _textInputState.value = text
    }

    fun clickLocation(location: Location) {
        clickLocationFlow.tryEmit(location)
    }
}