package com.joao.PlantSoS.network

import com.joao.PlantSoS.models.ForecastResponse
import com.joao.PlantSoS.models.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interface que define os endpoints da WEATHER API.
 */
interface WeatherApiService {

    @GET("v1/current.json")
    suspend fun getCurrentWeather(
        @Query("key") apiKey: String,
        @Query("q") location: String,
        @Query("aqi") aqi: String = "no",
        @Query("lang") lang: String = "pt"
    ): Response<WeatherResponse>

    @GET("v1/forecast.json")
    suspend fun getForecastWeather(
        @Query("key") apiKey: String,
        @Query("q") location: String,
        @Query("days") days: Int,
        @Query("aqi") aqi: String = "no",
        @Query("alerts") alerts: String = "no",
        @Query("lang") lang: String = "pt"
    ): Response<ForecastResponse>
}