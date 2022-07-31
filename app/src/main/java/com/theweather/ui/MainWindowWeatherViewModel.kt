package com.theweather.ui


import android.content.res.Resources
import android.util.Log
import androidx.lifecycle.*
import com.theweather.data.DataSource
import com.theweather.data.models.City
import com.theweather.data.models.ModelWeather
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedResults
import kotlinx.coroutines.launch

class MainWindowWeatherViewModel(private val dataSource: DataSource) : ViewModel() {

    private val _variantCitiesSearch = MutableLiveData<List<City>>()
    val variantCitiesSearch: LiveData<List<City>> = _variantCitiesSearch
    private val _currentCity = MutableLiveData<ModelWeather>()
    val currentCity: LiveData<ModelWeather> = _currentCity

    init {
        viewModelScope.launch {
            dataSource.getCity().asFlow().collect { results: ResultsChange<ModelWeather> ->
                when (results) {
                    is InitialResults<ModelWeather> -> {

                        if (!results.list.isEmpty()) {
                            val result = results.list.first()
                            _currentCity.postValue(results.list.first())
                            dataSource.updateTemperature(
                                result.currentcity,
                                result.latitude,
                                result.longitude
                            )
                        }


                    }
                    is UpdatedResults<ModelWeather> -> {
                        if (!results.list.isEmpty()) {

                            _currentCity.postValue(results.list.first())
                        }

                    }
                }
            }
        }
    }

    fun updateTemperature(city: String, latitude: String, longitude: String) {

        viewModelScope.launch {
            dataSource.updateTemperature(
                city,
                latitude,
                longitude
            )
        }

    }

    fun onQueryTextChange(newText: String?) {
        if (!newText.isNullOrEmpty()) {
            viewModelScope.launch {
                val weather = dataSource.serviceGeoWeather.findCity(newText)
                weather.body()?.let { cities ->
                    _variantCitiesSearch.postValue(cities.results)
                }
            }
        } else {
            _variantCitiesSearch.postValue(listOf())
        }
    }


}

class MainWindowWeatherViewModelFactory(val resources: Resources, val packageName: String) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainWindowWeatherViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainWindowWeatherViewModel(
                DataSource.getDataSource(resources, packageName)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}