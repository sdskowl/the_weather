package com.theweather.data.models

data class CurrenWeather(
    val weathercode: Int,
    val windspeed: String,
    val time: String,
    val winddirection: String,
    val temperature: String
)
