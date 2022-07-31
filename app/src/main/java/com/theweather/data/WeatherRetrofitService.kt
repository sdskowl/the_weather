package com.theweather.data

import com.theweather.data.models.WeatherSummary
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherRetrofitService {
    @GET("forecast")
    suspend fun getWeather(
        @Query("latitude") latitude: String,
        @Query("longitude") longitude: String,
        @Query("current_weather") current_weather: String = "true",
        @Query("timezone") timezone: String = "UTC",
        @Query("daily") daily: List<String> = listOf(
            "temperature_2m_max",
            "temperature_2m_min",
            "weathercode"
        )
    ): Response<WeatherSummary>

    companion object {
        private var retrofitService: WeatherRetrofitService? = null
        fun getInstance(): WeatherRetrofitService {

            if (retrofitService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://api.open-meteo.com/v1/")
                    .addConverterFactory(MoshiConverterFactory.create())
                    .build()
                retrofitService = retrofit.create(WeatherRetrofitService::class.java)
            }
            return retrofitService!!
        }
    }
}