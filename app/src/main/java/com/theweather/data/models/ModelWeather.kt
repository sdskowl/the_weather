package com.theweather.data.models

import io.realm.kotlin.types.RealmObject

class ModelWeather: RealmObject {
    var weathercode: String = ""
    var windspeed: String = ""
    var time: String = ""
    var winddirection: String = ""
    var temperature: String = ""
    var latitude: String = ""
    var longitude: String = ""
    var currentcity: String = ""

}