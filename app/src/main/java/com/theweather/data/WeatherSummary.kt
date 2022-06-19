package com.theweather.data

data class WeatherSummary (
    val latitude: String,
    val utc_offset_seconds: String,
    val longitude: String,
    val current_weather: CurrenWeather,
    val elevation: String,
    val generationtime_ms: String,
)

