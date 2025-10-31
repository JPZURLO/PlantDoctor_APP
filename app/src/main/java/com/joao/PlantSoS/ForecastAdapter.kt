package com.joao.PlantSoS

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.joao.PlantSoS.models.ForecastDay
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class ForecastAdapter(private var forecastDays: List<ForecastDay>) : RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder>() {

    class ForecastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.text_view_forecast_date)
        val iconImageView: ImageView = itemView.findViewById(R.id.image_view_forecast_icon)
        val tempTextView: TextView = itemView.findViewById(R.id.text_view_forecast_temp)
        val rainChanceTextView: TextView = itemView.findViewById(R.id.text_view_rain_chance)
        val humidityTextView: TextView = itemView.findViewById(R.id.text_view_forecast_humidity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.forecast_item, parent, false)
        return ForecastViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        val day = forecastDays[position]

        // Formatando a data
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val outputFormatter = DateTimeFormatter.ofPattern("dd 'de' MMMM", Locale.forLanguageTag("pt-BR"))
        val date = LocalDate.parse(day.date, inputFormatter)
        holder.dateTextView.text = date.format(outputFormatter)

        // ✅ TEMPERATURA: Agora usando maxtempC e mintempC
        holder.tempTextView.text = "${day.day.maxtempC.toInt()}°/${day.day.mintempC.toInt()}°"

        Glide.with(holder.itemView.context)
            .load("https:${day.day.condition.icon}")
            .into(holder.iconImageView)

        // LÓGICA DE CHUVA
        val chanceOfRain = day.day.dailyChanceOfRain
        if (chanceOfRain > 0) {
            holder.rainChanceTextView.visibility = View.VISIBLE
            holder.rainChanceTextView.text = "Chuva: $chanceOfRain%"
        } else {
            holder.rainChanceTextView.visibility = View.GONE
        }

        // LÓGICA DE UMIDADE
        val avgHumidity = day.day.avgHumidity.toInt()
        holder.humidityTextView.visibility = View.VISIBLE
        holder.humidityTextView.text = "Umidade: $avgHumidity%"
    }

    override fun getItemCount() = forecastDays.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateForecast(newForecastDays: List<ForecastDay>) {
        forecastDays = newForecastDays
        notifyDataSetChanged()
    }
}