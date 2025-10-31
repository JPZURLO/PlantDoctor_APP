package com.joao.PlantSoS.repository

import android.util.Log
import com.joao.PlantSoS.models.ForecastResponse
import com.joao.PlantSoS.models.WeatherResponse
import com.joao.PlantSoS.network.RetrofitClient

class WeatherRepository {

    // Usando o serviço da API de Tempo
    private val apiService = RetrofitClient.weatherApiService
    private val apiKey = "SUA_CHAVE_DA_API_DE_TEMPO_AQUI" // Lembre-se de colocar sua chave

    suspend fun getCurrentWeather(location: String): WeatherResponse? {
        try {
            val response = apiService.getCurrentWeather(apiKey = apiKey, location = location)
            if (response.isSuccessful) {
                return response.body()
            } else {
                Log.e("WeatherRepository", "Erro ao buscar tempo atual: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e("WeatherRepository", "Exceção ao buscar tempo atual", e)
        }
        return null
    }

    suspend fun getForecast(location: String, days: Int): ForecastResponse? {
        // ... (lógica similar para a previsão do tempo)
        return null
    }
}