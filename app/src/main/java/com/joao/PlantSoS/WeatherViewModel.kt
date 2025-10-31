package com.joao.PlantSoS

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joao.PlantSoS.models.ForecastDay
import com.joao.PlantSoS.models.WeatherResponse
import com.joao.PlantSoS.network.RetrofitClient
import com.joao.PlantSoS.network.WeatherApiService // Garanta que este import está correto
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {

    private val apiKey = "b61fcaf6ce154efd962104910253009"
    // ▼▼▼ A CORREÇÃO ESTÁ NESTA LINHA ▼▼▼
    private val apiService: WeatherApiService = RetrofitClient.weatherApiService


    private val _weatherData = MutableLiveData<Result<WeatherResponse>>()
    val weatherData: LiveData<Result<WeatherResponse>> get() = _weatherData

    private val _forecastData = MutableLiveData<Result<List<ForecastDay>>>()
    val forecastData: LiveData<Result<List<ForecastDay>>> get() = _forecastData

    fun fetchAllWeatherData(location: String) {
        fetchCurrentWeather(location)
        fetchForecastWeather(location)
    }

    private fun fetchCurrentWeather(location: String) {
        viewModelScope.launch {
            try {
                // Agora esta chamada vai funcionar, pois getCurrentWeather existe em WeatherApiService
                val response = apiService.getCurrentWeather(apiKey, location)
                if (response.isSuccessful && response.body() != null) {
                    _weatherData.postValue(Result.success(response.body()!!))
                } else {
                    _weatherData.postValue(Result.failure(Exception("Erro ao buscar dados do tempo.")))
                }
            } catch (e: Exception) {
                _weatherData.postValue(Result.failure(Exception("Falha na conexão: ${e.message}")))
            }
        }
    }

    private fun fetchForecastWeather(location: String) {
        viewModelScope.launch {
            try {
                // Agora esta chamada vai funcionar, pois getForecastWeather existe em WeatherApiService
                val response = apiService.getForecastWeather(apiKey, location, 3)
                if (response.isSuccessful && response.body() != null) {
                    _forecastData.postValue(Result.success(response.body()!!.forecast.forecastday))
                } else {
                    _forecastData.postValue(Result.failure(Exception("Erro ao buscar previsão do tempo.")))
                }
            } catch (e: Exception) {
                _forecastData.postValue(Result.failure(Exception("Falha na conexão: ${e.message}")))
            }
        }
    }
}