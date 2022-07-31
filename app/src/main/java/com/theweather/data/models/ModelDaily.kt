package com.theweather.data.models

import io.realm.kotlin.types.RealmObject

class ModelDaily : RealmObject {
    var icon: Int = 0
    var day: String = ""
    var tempMax: Int = 0
    var tempMin: Int = 0
    var weatherCode: Int = 0
    var weatherCodeString: String = ""
}