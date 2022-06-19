package com.theweather.data

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface InterfaceGeoWeatherRetrofit {
    @GET("search")
    suspend fun findCity(@Query("name") name: String): Response<Cities>
}