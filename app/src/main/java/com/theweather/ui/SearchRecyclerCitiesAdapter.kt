package com.theweather.ui

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.theweather.data.models.City
import com.theweather.databinding.MainWindowWeatherRecyclerSearchBinding

class SearchRecyclerCitiesAdapter(private val onItemClicked: (city: City) -> Unit) :
    ListAdapter<City, SearchRecyclerCitiesViewHolder>(SearchRecyclerCitiesDiffCallback) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchRecyclerCitiesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = MainWindowWeatherRecyclerSearchBinding.inflate(inflater, parent, false)
        return SearchRecyclerCitiesViewHolder(binding, onItemClicked)
    }

    override fun onBindViewHolder(holder: SearchRecyclerCitiesViewHolder, position: Int) {
        holder.bind(currentList[position])

    }

    override fun getItemCount(): Int = currentList.size

    object SearchRecyclerCitiesDiffCallback : DiffUtil.ItemCallback<City>() {
        override fun areItemsTheSame(oldItem: City, newItem: City): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: City, newItem: City): Boolean {
            return oldItem.id == newItem.id && oldItem.name == newItem.name
        }
    }
}

class SearchRecyclerCitiesViewHolder(
    private val binding: MainWindowWeatherRecyclerSearchBinding,
    private val onItemClicked: (city: City) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    private var currentCity: City? = null

    init {
        binding.searchSuggestions.setOnClickListener { cityView ->
            currentCity?.let {
                onItemClicked(it)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    fun bind(city: City) {
        currentCity = city
        binding.citySugg.text = "${city.name} ${city.admin1} ${city.country}"
    }
}