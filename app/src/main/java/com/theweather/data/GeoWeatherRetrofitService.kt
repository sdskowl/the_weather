package com.theweather.data

import com.theweather.data.models.Cities
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface GeoWeatherRetrofitService {
    @GET("search")
    suspend fun findCity(@Query("name") name: String): Response<Cities>

    companion object {
        private var retrofitService: GeoWeatherRetrofitService? = null
        fun getInstance(): GeoWeatherRetrofitService {

            if (retrofitService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://geocoding-api.open-meteo.com/v1/")
                    .addConverterFactory(MoshiConverterFactory.create())
                    .build()
                retrofitService = retrofit.create(GeoWeatherRetrofitService::class.java)
            }
            return retrofitService!!
        }
    }
}