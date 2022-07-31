package com.theweather.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.theweather.R
import com.theweather.data.models.City
import com.theweather.databinding.MainWindowWeatherFragmentBinding
import java.util.*


class MainWindowWeather : Fragment() {

    private lateinit var viewModel: MainWindowWeatherViewModel
    private var _binding: MainWindowWeatherFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var inputMethodManager: InputMethodManager? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = MainWindowWeatherFragmentBinding.inflate(inflater, container, false)
        inputMethodManager =
            requireActivity().getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager?
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            MainWindowWeatherViewModelFactory(resources, requireContext().packageName)
        )[MainWindowWeatherViewModel::class.java]
        val searchAdapter = SearchRecyclerCitiesAdapter { city -> onListItemClick(city) }
        val daysAdapter = WeatherDaysRecyclerAdapter()
        binding.recyclerSearch.adapter = searchAdapter
        binding.recyclerSearch.layoutManager = LinearLayoutManager(requireContext())
        binding.weatherDays.adapter = daysAdapter
        binding.weatherDays.layoutManager = LinearLayoutManager(requireContext())
        binding.searchText.doOnTextChanged { text, start, before, count ->
            viewModel.onQueryTextChange(text.toString())
        }
        binding.searchButton.setOnClickListener {
            startSearch()
        }
        binding.searchCancelButton.setOnClickListener {
            stopSearch()
        }
        binding.searchText.setOnFocusChangeListener { viewText, _ ->
            if (!viewText.isFocused) {
                stopSearch()
            }
        }
        viewModel.variantCitiesSearch.observe(viewLifecycleOwner) { listCity ->
            if (listCity == null) {
                searchAdapter.submitList(listOf())
            } else {
                searchAdapter.submitList(listCity)
            }
        }
        viewModel.currentCity.observe(viewLifecycleOwner) { currentCity ->
            currentCity?.let {
                binding.city.text = currentCity.currentcity
                binding.temperature.text = currentCity.temperature + "°C"
                binding.weatherCode.text = currentCity.weatherString
                binding.windSpeed.text = "Wind " + currentCity.windspeed + " Km/h"
                currentBackground(currentCity.time.toIntOrNull())
                binding.icWeather.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        currentCity.icon
                    )
                )
                daysAdapter.submitList(currentCity.days)
            }
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        updateCity()

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (binding.searchText.isFocused) {
                        stopSearch()
                    } else {
                        requireActivity().finish()
                    }
                }

            })
    }


    private fun startSearch() {
        with(binding) {
            searchButton.visibility = View.GONE
            searchCancelButton.visibility = View.VISIBLE
            binding.searchBar.background =
                ContextCompat.getDrawable(requireActivity(), R.drawable.bg_white_rounded)
            //show keyboard
            searchText.visibility = View.VISIBLE
            searchText.requestFocus()
            inputMethodManager?.showSoftInput(searchText, 0)

        }
    }

    private fun stopSearch() {
        with(binding) {
            searchButton.visibility = View.VISIBLE
            searchCancelButton.visibility = View.GONE
            binding.searchBar.background = null
            //hide keyboard
            searchText.clearFocus()
            searchText.text.clear()
            searchText.visibility = View.INVISIBLE
            inputMethodManager?.hideSoftInputFromWindow(searchText.windowToken, 0)
        }
    }

    private fun onListItemClick(city: City) {
        stopSearch()
        viewModel.updateTemperature(city.name, city.latitude, city.longitude)
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
    private fun updateCity() {
        requireActivity().let {
            if (hasPermissions(requireActivity() as Context, PERMISSIONS)) {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        location?.let {
                            val city: Address? = Geocoder(
                                requireContext(),
                                Locale.getDefault()
                            ).getFromLocation(location.latitude, location.longitude, 1)[0]
                            city?.let {
                                viewModel.updateTemperature(
                                    city.locality,
                                    city.latitude.toString(),
                                    city.longitude.toString()
                                )

                            }
                        }
                    }
            } else {
                permReqLauncher.launch(
                    PERMISSIONS
                )
            }
        }
    }


    private fun hasPermissions(context: Context, permissions: Array<String>): Boolean =
        permissions.all {
            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

    private fun currentBackground(time: Int?) {
        time?.let { t ->
            when (t) {
                in 5..17 -> {
                    binding.mainLayout.setBackgroundResource(R.drawable.morning)
                }
                in 18..21 -> {
                    binding.mainLayout.setBackgroundResource(R.drawable.evening)
                }
                else -> {
                    binding.mainLayout.setBackgroundResource(R.drawable.night)
                }
            }
        }
    }


}