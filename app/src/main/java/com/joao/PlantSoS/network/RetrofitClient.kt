package com.joao.PlantSoS.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val PLANT_DOCTOR_BASE_URL = "https://plantdoctor-backend.onrender.com/"
    private const val WEATHER_API_BASE_URL = "https://api.weatherapi.com/"

    private val plantDoctorRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(PLANT_DOCTOR_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val weatherApiRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(WEATHER_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // ✅ CORRIGIDO: Agora usa a interface correta
    val plantDoctorApiService: PlantDoctorApiService by lazy {
        plantDoctorRetrofit.create(PlantDoctorApiService::class.java)
    }

    // ✅ CORRIGIDO: Agora usa a interface correta
    val weatherApiService: WeatherApiService by lazy {
        weatherApiRetrofit.create(WeatherApiService::class.java)
    }
}