package com.theweather.data.models

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject

class ModelWeather : RealmObject {
    var weathercode: Int = 0
    var windspeed: String = ""
    var time: String = ""
    var winddirection: String = ""
    var temperature: String = ""
    var latitude: String = ""
    var longitude: String = ""
    var currentcity: String = ""
    var days: RealmList<ModelDaily> = realmListOf()
    var weatherString: String = ""
    var icon: Int = 0
}