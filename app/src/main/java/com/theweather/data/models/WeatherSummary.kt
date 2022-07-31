package com.theweather.data.models

data class WeatherSummary(
    val latitude: String,
    val longitude: String,
    val generationtime_ms: String,
    val utc_offset_seconds: String,
    val elevation: String,
    val current_weather: CurrenWeather,
    val daily_units: DailyUnits,
    val daily: Daily
)

