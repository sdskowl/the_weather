package com.theweather.ui


import android.location.Address
import android.util.Log
import android.widget.SearchView
import androidx.lifecycle.*
import com.theweather.data.City
import com.theweather.data.DataSource
import com.theweather.data.models.ModelWeather
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedResults
import kotlinx.coroutines.launch

class MainWindowWeatherViewModel(private val dataSource: DataSource) : ViewModel(),
    SearchView.OnQueryTextListener {

    private val _variantCitiesSearch = MutableLiveData<List<City>>()
    val variantCitiesSearch: LiveData<List<City>> = _variantCitiesSearch
    private val _currentCity = MutableLiveData<ModelWeather>()
    val currentCity: LiveData<ModelWeather> = _currentCity

    init {
        viewModelScope.launch {
            dataSource.getCity().asFlow().collect { results: ResultsChange<ModelWeather> ->
                when (results) {
                    is InitialResults<ModelWeather> -> {
                        if(!results.list.isNullOrEmpty()){
                            _currentCity.postValue(results.list.first())
                            dataSource.updateTemperatureWithRealm(results.list.first())
                        }

                    }
                    is UpdatedResults<ModelWeather> -> {
                        if(!results.list.isNullOrEmpty()){
                            _currentCity.postValue(results.list.first())
                        }

                    }
                }
            }
        }
    }


    fun changeCity(city: City) {
        viewModelScope.launch {
            dataSource.changeCity(city)
        }
    }
    fun updateWithGeoData(city: Address?){
        viewModelScope.launch {
            dataSource.updateWithGeoData(city)
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        newText?.let {
            viewModelScope.launch {
                val weather = dataSource.serviceGeoWeather.findCity(newText)
                Log.d("weather", weather.body().toString())
                weather.body()?.let { cities ->
                    _variantCitiesSearch.postValue(cities.results)
                }
            }
        }
        return true
    }


}

class MainWindowWeatherViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainWindowWeatherViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainWindowWeatherViewModel(
                DataSource.getDataSource()

            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}