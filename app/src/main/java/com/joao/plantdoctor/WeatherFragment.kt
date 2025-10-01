package com.joao.plantdoctor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.joao.plantdoctor.models.WeatherResponse

class WeatherFragment : Fragment() {

    private val viewModel: WeatherViewModel by viewModels()

    // ✅ 1. ADICIONAR REFERÊNCIAS PARA O ADAPTER E RECYCLERVIEW
    private lateinit var forecastAdapter: ForecastAdapter
    private lateinit var forecastRecyclerView: RecyclerView

    // Views para o tempo ATUAL
    private lateinit var progressBar: ProgressBar
    private lateinit var locationTextView: TextView
    private lateinit var temperatureTextView: TextView
    private lateinit var conditionTextView: TextView
    private lateinit var weatherIconImageView: ImageView
    private lateinit var feelsLikeTextView: TextView
    private lateinit var humidityTextView: TextView
    private lateinit var windTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_weather, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializa as views do tempo atual
        progressBar = view.findViewById(R.id.progress_bar)
        locationTextView = view.findViewById(R.id.text_view_location)
        temperatureTextView = view.findViewById(R.id.text_view_temperature)
        conditionTextView = view.findViewById(R.id.text_view_condition)
        weatherIconImageView = view.findViewById(R.id.image_view_weather_icon)
        feelsLikeTextView = view.findViewById(R.id.text_view_feels_like)
        humidityTextView = view.findViewById(R.id.text_view_humidity)
        windTextView = view.findViewById(R.id.text_view_wind)

        // ✅ 2. CHAMAR A CONFIGURAÇÃO DO RECYCLERVIEW
        setupRecyclerView(view)
        setupObservers()

        // ✅ 3. ALTERAR A CHAMADA PARA BUSCAR TODOS OS DADOS (ATUAL E PREVISÃO)
        // Você pode trocar "Sorocaba" pela cidade que quiser como padrão.
        viewModel.fetchAllWeatherData("Sorocaba")
    }

    // ✅ 4. ADICIONAR A FUNÇÃO PARA CONFIGURAR O RECYCLERVIEW E O ADAPTER
    private fun setupRecyclerView(view: View) {
        forecastRecyclerView = view.findViewById(R.id.recycler_view_forecast)
        forecastAdapter = ForecastAdapter(emptyList()) // Começa com uma lista vazia
        forecastRecyclerView.adapter = forecastAdapter
        forecastRecyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun setupObservers() {
        // Observador para o tempo ATUAL (código que você já tinha)
        viewModel.weatherData.observe(viewLifecycleOwner) { result ->
            progressBar.visibility = View.GONE
            result.onSuccess { weatherResponse ->
                updateUI(weatherResponse)
            }
            result.onFailure { error ->
                Toast.makeText(context, "Erro: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }

        // ✅ 5. ADICIONAR O NOVO OBSERVADOR PARA OS DADOS DA PREVISÃO
        viewModel.forecastData.observe(viewLifecycleOwner) { result ->
            result.onSuccess { forecastDays ->
                // Atualiza o adapter com a nova lista de dias
                forecastAdapter.updateForecast(forecastDays)
            }
            result.onFailure { error ->
                Toast.makeText(context, "Erro ao buscar previsão: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUI(data: WeatherResponse) {
        // ... (esta função continua exatamente igual)
        locationTextView.text = "${data.location.name}, ${data.location.region}"
        temperatureTextView.text = "${data.current.tempC.toInt()}°C"
        conditionTextView.text = data.current.condition.text
        feelsLikeTextView.text = "Sensação: ${data.current.feelslikeC.toInt()}°C"
        humidityTextView.text = "Umidade: ${data.current.humidity}%"
        windTextView.text = "Vento: ${data.current.windKph.toInt()} km/h"

        Glide.with(this)
            .load("https:${data.current.condition.icon}")
            .into(weatherIconImageView)
    }
}