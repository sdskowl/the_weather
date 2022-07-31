package com.theweather.data.models

data class Daily(
    val time: List<String>,
    val temperature_2m_max: List<Double>,
    val temperature_2m_min: List<Double>,
    val weathercode: List<Int>
)
