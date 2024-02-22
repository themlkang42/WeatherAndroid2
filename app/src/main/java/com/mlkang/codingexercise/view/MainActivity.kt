package com.mlkang.codingexercise.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.mlkang.codingexercise.model.Location
import com.mlkang.codingexercise.model.Weather
import com.mlkang.codingexercise.ui.theme.CodingExerciseTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CodingExerciseTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WeatherScreen()
                }
            }
        }
    }
}

@Composable
fun WeatherScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = viewModel()
) {
    Column(modifier = modifier) {
        val textState by viewModel.textInputState.collectAsStateWithLifecycle()
        TextField(
            value = textState,
            onValueChange = { viewModel.updateTextInput(it) },
            placeholder = { Text("Search for a city") },
            modifier = Modifier.fillMaxWidth()
        )
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier
        ) {
            val weather by viewModel.weatherState.collectAsStateWithLifecycle()
            weather?.let {
                WeatherInformation(it)
            }
            LocationsDropdown()
        }
    }
}

@Composable
fun WeatherInformation(weather: Weather, modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            weather.name,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(weather.main.temp.roundToInt().toString() + "\u2109")
        weather.conditions.firstOrNull()?.let { condition ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(condition.description.capitalize(Locale.current))
                AsyncImage(
                    model = "https://openweathermap.org/img/wn/${condition.icon}@2x.png",
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }
}

@Composable
fun LocationsDropdown(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = viewModel()
) {
    val showLocations by viewModel.showLocationsState.collectAsStateWithLifecycle()
    if (showLocations) {
        val locations by viewModel.locationsState.collectAsStateWithLifecycle()
        LazyColumn(
            modifier = modifier.background(Color.LightGray)
        ) {
            items(locations) { location ->
                LocationItem(location)
            }
        }
    }
}

@Composable
fun LocationItem(
    location: Location,
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = viewModel()
) {
    val sb = StringBuilder()
    sb.append(location.name)
    if (location.state != null) {
        sb.append(", ${location.state}")
    }
    sb.append(", ${location.country}")
    val text = sb.toString()

    Text(
        text,
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                viewModel.clickLocation(location)
            }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}