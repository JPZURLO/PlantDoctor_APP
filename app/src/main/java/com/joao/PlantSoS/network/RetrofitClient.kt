package com.joao.PlantSoS.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // URL base do seu backend Flask (Render)
    private const val PLANT_DOCTOR_BASE_URL = "https://plantdoctor-backend.onrender.com/api/"
    private const val WEATHER_API_BASE_URL = "https://api.weatherapi.com/"

    // Instância do Retrofit para o backend Flask
    private val plantDoctorRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(PLANT_DOCTOR_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Instância do Retrofit para a API de clima
    private val weatherApiRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(WEATHER_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Serviço de IA do PlantDoctor (Flask)
    val plantDoctorApiService: PlantDoctorApiService by lazy {
        plantDoctorRetrofit.create(PlantDoctorApiService::class.java)
    }

    // Serviço de Clima
    val weatherApiService: WeatherApiService by lazy {
        weatherApiRetrofit.create(WeatherApiService::class.java)
    }


}
