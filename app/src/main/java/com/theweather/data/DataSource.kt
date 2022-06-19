package com.theweather.data

import android.location.Address
import com.theweather.data.models.ModelWeather
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*

class DataSource {
    val config = RealmConfiguration.Builder(setOf(ModelWeather::class))
        .deleteRealmIfMigrationNeeded()
        .build()

    private val realm = Realm.open(config)
    private val serviceWeather = Retrofit.Builder()
        .baseUrl("https://api.open-meteo.com/v1/")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
        .create(InterfaceWeatherRetrofit::class.java)
    val serviceGeoWeather = Retrofit.Builder()
        .baseUrl("https://geocoding-api.open-meteo.com/v1/")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
        .create(InterfaceGeoWeatherRetrofit::class.java)


    fun getCity(): RealmResults<ModelWeather> {
        return realm.query<ModelWeather>().find()
    }
    suspend fun updateTemperatureWithRealm(city: ModelWeather) {
        val weather = serviceWeather.getWeather(city.latitude, city.longitude, "true")
        weather.body()?.let { weatherSummary ->
            realm.writeBlocking {
                val cityOld:RealmResults<ModelWeather> = this.query<ModelWeather>().find()
                delete(cityOld)
                this.copyToRealm(ModelWeather().apply {
                    this.currentcity = city.currentcity
                    this.latitude = city.latitude
                    this.longitude = city.longitude
                    this.windspeed = weatherSummary.current_weather.windspeed
                    this.winddirection = weatherSummary.current_weather.winddirection
                    this.weathercode = weatherSummary.current_weather.weathercode
                    this.time = Date().toString()
                    this.temperature = weatherSummary.current_weather.temperature
                })
            }
        }
    }
    suspend fun changeCity(city: City){
        val weather = serviceWeather.getWeather(city.latitude, city.longitude, "true")
        weather.body()?.let { weatherSummary ->
            realm.writeBlocking {
                val cityOld:RealmResults<ModelWeather> = this.query<ModelWeather>().find()
                delete(cityOld)

                this.copyToRealm(ModelWeather().apply {
                    this.currentcity = city.name
                    this.latitude = city.latitude
                    this.longitude = city.longitude
                    this.windspeed = weatherSummary.current_weather.windspeed
                    this.winddirection = weatherSummary.current_weather.winddirection
                    this.weathercode = weatherSummary.current_weather.weathercode
                    this.time = Date().toString()
                    this.temperature = weatherSummary.current_weather.temperature
                })
            }
        }


    }
    suspend fun updateWithGeoData(city: Address?) {
        if (city != null) {
            val weather = serviceWeather.getWeather(city.latitude.toString(), city.longitude.toString(), "true")
            weather.body()?.let { weatherSummary ->
                realm.writeBlocking {
                    val cityOldd: RealmResults<ModelWeather> = this.query<ModelWeather>().find()
                    delete(cityOldd)
                    this.copyToRealm(ModelWeather().apply {
                        this.currentcity = city.locality
                        this.latitude = city.latitude.toString()
                        this.longitude = city.longitude.toString()
                        this.windspeed = weatherSummary.current_weather.windspeed
                        this.winddirection = weatherSummary.current_weather.winddirection
                        this.weathercode = weatherSummary.current_weather.weathercode
                        this.time = Date().toString()
                        this.temperature = weatherSummary.current_weather.temperature
                    })
                }
            }
        }

    }

    companion object {
        private var INSTANCE: DataSource? = null

        fun getDataSource(): DataSource {
            return synchronized(DataSource::class) {
                val newInstance = INSTANCE ?: DataSource()
                INSTANCE = newInstance
                newInstance
            }
        }
    }
}