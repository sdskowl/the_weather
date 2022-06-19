package com.theweather.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.theweather.data.City
import com.theweather.databinding.MainWindowWeatherFragmentBinding
import java.util.*

class MainWindowWeather : Fragment() {

    private lateinit var viewModel: MainWindowWeatherViewModel
    private var _binding: MainWindowWeatherFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = MainWindowWeatherFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            MainWindowWeatherViewModelFactory()
        )[MainWindowWeatherViewModel::class.java]
        binding.searchView.setOnQueryTextListener(viewModel)
        binding.searchView.isSubmitButtonEnabled = false
        val searchAdapter = SearchRecyclerCitiesAdapter { city -> onListItemClick(city) }
        binding.recyclerSearch.adapter = searchAdapter
        viewModel.variantCitiesSearch.observe(viewLifecycleOwner) { listCity ->
            if (listCity == null) {
                searchAdapter.submitList(listOf())
            } else {
                searchAdapter.submitList(listCity)
            }
        }
        viewModel.currentCity.observe(viewLifecycleOwner) { currentCity ->
            currentCity?.let {
                var weatherString = ""
                try {
                    val weatherStringId = resources.getIdentifier(
                        "weathercode_${currentCity.weathercode}",
                        "string",
                        requireContext().packageName
                    )
                    weatherString = resources.getString(weatherStringId)
                } catch (e: Exception) {
                }
                binding.city.text = currentCity.currentcity
                binding.temperature.text = currentCity.temperature + "°C"
                binding.timeUpdate.text = currentCity.time
                binding.weathercode.text = weatherString
                binding.windspeed.text = "Wind " + currentCity.windspeed + " Km/h"
            }
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        updateCity()
        
    }

    private fun onListItemClick(city: City) {
        binding.searchView.onActionViewCollapsed()
        viewModel.changeCity(city)

    }

    companion object {
        var PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    private val permReqLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->

            val granted = permissions.entries.all {
                it.value == true
            }
            if (granted) {
                updateCity()
            }
        }

    @SuppressLint("MissingPermission")
    private fun updateCity(){
        requireActivity().let {
            if (hasPermissions(requireActivity() as Context, PERMISSIONS)) {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        location?.let {
                            val city: Address? = Geocoder(
                                requireContext(),
                                Locale.getDefault()
                            ).getFromLocation(location.latitude, location.longitude, 1)[0]
                            viewModel.updateWithGeoData(city)
                        }
                    }
            } else {
                permReqLauncher.launch(
                    PERMISSIONS
                )
            }
        }
    }


    private fun hasPermissions(context: Context, permissions: Array<String>): Boolean = permissions.all {
        ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }


}