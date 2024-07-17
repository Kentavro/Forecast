package com.alexhey.newweatherapp.data

import android.service.notification.Condition

// Это должно быть на уровне domain
data class WeatherModel(
    val city: String,
    val time: String,
    val currentTemp: String,
    val condition: String,
    val icon: String,
    val maxTemp: String,
    val minTemp: String,
    val hours: String

)
