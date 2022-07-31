package com.theweather.data

import android.annotation.SuppressLint
import android.content.res.Resources
import android.util.Log
import com.theweather.R
import com.theweather.data.models.ModelDaily
import com.theweather.data.models.ModelWeather
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.query.RealmResults
import java.text.SimpleDateFormat

class DataSource(private val resources: Resources, private val packageName: String) {
    private val config = RealmConfiguration.Builder(setOf(ModelWeather::class, ModelDaily::class))
        .deleteRealmIfMigrationNeeded()
        .build()

    private val realm = Realm.open(config)
    private val serviceWeather = WeatherRetrofitService.getInstance()
    val serviceGeoWeather = GeoWeatherRetrofitService.getInstance()
    fun getCity(): RealmResults<ModelWeather> {
        return realm.query<ModelWeather>().find()
    }

    suspend fun updateTemperature(city: String, latitude: String, longitude: String) {
        val weather = serviceWeather.getWeather(latitude, longitude)
        weather.body()?.let { weatherSummary ->

            realm.writeBlocking {
                val cityOld: RealmResults<ModelWeather> = this.query<ModelWeather>().find()
                delete(cityOld)
                this.copyToRealm(ModelWeather().apply {
                    val formattedTime = formatTime(weatherSummary.current_weather.time)
                    this.currentcity = city
                    this.latitude = latitude
                    this.longitude = longitude
                    this.windspeed = weatherSummary.current_weather.windspeed
                    this.winddirection = weatherSummary.current_weather.winddirection
                    this.weathercode = weatherSummary.current_weather.weathercode
                    this.time = formattedTime
                    this.icon = getCurrentIcon(formattedTime.toInt(), this.weathercode)
                    this.temperature = weatherSummary.current_weather.temperature
                    this.weatherString =
                        getStringWeatherCode(weatherSummary.current_weather.weathercode.toString())
                    val realmList = realmListOf<ModelDaily>()
                    with(weatherSummary.daily) {
                        for (i in time.indices) {
                            val item = ModelDaily()
                            item.day = if (i == 0) "Today" else time[i].formatDayToSymbols()
                            item.tempMin = temperature_2m_min[i].toInt()
                            item.tempMax = temperature_2m_max[i].toInt()
                            item.weatherCode = weathercode[i]
                            item.weatherCodeString = getStringWeatherCode(weathercode[i].toString())
                            item.icon = getCurrentIcon(12, weathercode[i])
                            realmList.add(item)
                        }
                    }
                    this.days = realmList
                })
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun formatTime(time: String): String {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm")
        val formatter = SimpleDateFormat("HH")
        return parser.parse(time)
            ?.let { formatter.format(it) }.toString()
    }

    @SuppressLint("SimpleDateFormat")
    fun String.formatDayToSymbols(): String {
        val parser = SimpleDateFormat("yyyy-MM-dd")
        val formatter = SimpleDateFormat("EEE")
        return parser.parse(this)
            ?.let { formatter.format(it) }.toString()
    }

    fun getCurrentIcon(time: Int?, weatherCode: Int): Int {
        var result = R.drawable.ic_cloudy
        time?.let { t ->
            if (t in 8..17) {
                val icons = listIconsDay()
                if (weatherCode in icons.keys) {
                    result = icons[weatherCode]!!
                }
            } else {
                val icons = listIconsNight()
                if (weatherCode in icons.keys) {
                    result = icons[weatherCode]!!
                }
            }
        }
        return result
    }

    private fun listIconsDay(): Map<Int, Int> {
        return mapOf(
            0 to R.drawable.ic_day,
            1 to R.drawable.ic_cloudy_day_1,
            2 to R.drawable.ic_cloudy_day_2,
            3 to R.drawable.ic_cloudy_day_3,
            45 to R.drawable.ic_cloudy,
            48 to R.drawable.ic_cloudy,
            51 to R.drawable.ic_rainy_1,
            53 to R.drawable.ic_rainy_2,
            55 to R.drawable.ic_rainy_3,
            56 to R.drawable.ic_rainy_1,
            57 to R.drawable.ic_rainy_3,
            61 to R.drawable.ic_rainy_1,
            63 to R.drawable.ic_rainy_2,
            65 to R.drawable.ic_rainy_3,
            66 to R.drawable.ic_rainy_1,
            67 to R.drawable.ic_rainy_3,
            71 to R.drawable.ic_snowy_1,
            73 to R.drawable.ic_snowy_2,
            75 to R.drawable.ic_snowy_3,
            77 to R.drawable.ic_snowy_3,
            80 to R.drawable.ic_rainy_1,
            81 to R.drawable.ic_rainy_2,
            82 to R.drawable.ic_rainy_3,
            85 to R.drawable.ic_snowy_1,
            86 to R.drawable.ic_snowy_3,
            95 to R.drawable.ic_thunder,
            96 to R.drawable.ic_thunder,
            99 to R.drawable.ic_thunder,
        )
    }

    private fun listIconsNight(): Map<Int, Int> {
        return mapOf(
            0 to R.drawable.ic_night,
            1 to R.drawable.ic_cloudy_night_1,
            2 to R.drawable.ic_cloudy_night_2,
            3 to R.drawable.ic_cloudy_night_3,
            45 to R.drawable.ic_cloudy,
            48 to R.drawable.ic_cloudy,
            51 to R.drawable.ic_rainy_5,
            53 to R.drawable.ic_rainy_6,
            55 to R.drawable.ic_rainy_7,
            56 to R.drawable.ic_rainy_5,
            57 to R.drawable.ic_rainy_7,
            61 to R.drawable.ic_rainy_5,
            63 to R.drawable.ic_rainy_6,
            65 to R.drawable.ic_rainy_7,
            66 to R.drawable.ic_rainy_5,
            67 to R.drawable.ic_rainy_7,
            71 to R.drawable.ic_snowy_4,
            73 to R.drawable.ic_snowy_5,
            75 to R.drawable.ic_snowy_6,
            77 to R.drawable.ic_snowy_6,
            80 to R.drawable.ic_rainy_5,
            81 to R.drawable.ic_rainy_6,
            82 to R.drawable.ic_rainy_7,
            85 to R.drawable.ic_snowy_4,
            86 to R.drawable.ic_snowy_6,
            95 to R.drawable.ic_thunder,
            96 to R.drawable.ic_thunder,
            99 to R.drawable.ic_thunder,
        )
    }

    private fun getStringWeatherCode(code: String): String {
        var weatherString = ""
        try {
            val weatherStringId = resources.getIdentifier(
                "weathercode_$code",
                "string",
                packageName
            )
            weatherString = resources.getString(weatherStringId)

        } catch (e: Exception) {

        }
        return weatherString
    }

    companion object {
        private var INSTANCE: DataSource? = null

        fun getDataSource(resources: Resources, packageName: String): DataSource {
            return synchronized(DataSource::class) {
                val newInstance = INSTANCE ?: DataSource(resources, packageName)
                INSTANCE = newInstance
                newInstance
            }
        }
    }
}