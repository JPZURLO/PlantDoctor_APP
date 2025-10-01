package com.joao.plantdoctor.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // URL Base para a sua API Plant Doctor
    private const val PLANT_DOCTOR_BASE_URL = "https://plantdoctor-backend.onrender.com/"

    // URL Base para a API de Tempo
    private const val WEATHER_API_BASE_URL = "https://api.weatherapi.com/"

    // Cliente Retrofit para a API Plant Doctor
    private val plantDoctorRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(PLANT_DOCTOR_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Cliente Retrofit para a API de Tempo
    private val weatherApiRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(WEATHER_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Serviço que usa o cliente da Plant Doctor API
    val plantDoctorApiService: ApiService by lazy {
        plantDoctorRetrofit.create(ApiService::class.java)
    }

    // Serviço que usa o cliente da Weather API
    val weatherApiService: ApiService by lazy {
        weatherApiRetrofit.create(ApiService::class.java)
    }
}