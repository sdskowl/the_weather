package com.theweather.data

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface InterfaceWeatherRetrofit {
    @GET("forecast")
    suspend fun getWeather( @Query("latitude") latitude: String,
                            @Query("longitude") longitude: String,
                            @Query("current_weather") current_weather: String
                            ): Response<WeatherSummary>

}