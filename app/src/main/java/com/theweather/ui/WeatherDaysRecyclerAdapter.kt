package com.theweather.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.theweather.data.models.ModelDaily
import com.theweather.databinding.WeatherDaysRecyclerBinding

class WeatherDaysRecyclerAdapter :
    ListAdapter<ModelDaily, WeatherDaysRecyclerAdapter.WeatherDaysViewHolder>(
        WeatherDaysDiffCallback
    ) {

    class WeatherDaysViewHolder(private val binding: WeatherDaysRecyclerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(day: ModelDaily) {
            with(binding) {
                this.day.text = day.day
                this.maxTemp.text = "${day.tempMax}°C\nmax"
                this.minTemp.text = " ${day.tempMin}°C\nmin"
                this.weatherCodeString.text = day.weatherCodeString
                this.icWeatherDays.setImageDrawable(
                    ContextCompat.getDrawable(
                        this.root.context,
                        day.icon
                    )
                )
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherDaysViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = WeatherDaysRecyclerBinding.inflate(inflater, parent, false)
        return WeatherDaysViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WeatherDaysViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    object WeatherDaysDiffCallback : DiffUtil.ItemCallback<ModelDaily>() {
        override fun areItemsTheSame(oldItem: ModelDaily, newItem: ModelDaily): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ModelDaily, newItem: ModelDaily): Boolean {
            return oldItem.day == newItem.day
        }

    }
}