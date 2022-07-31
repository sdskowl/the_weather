package com.theweather.data.models

data class City(
    val id: String,
    val name: String,
    val latitude: String,
    val longitude: String,
    val ranking: String,
    val elevation: String,
    val feature_code: String,
    val country_code: String,
    val admin1_id: String,
    val admin3_id: String,
    val admin4_id: String,
    val timezone: String,
    val population: String,
    val postcodes: List<String>,
    val country_id: String,
    val country: String,
    val admin1: String,
    val admin3: String,
    val admin4: String
)

data class Cities(
    val results: List<City>
)
