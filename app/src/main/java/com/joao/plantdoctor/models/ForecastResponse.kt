package com.joao.plantdoctor.models

import com.google.gson.annotations.SerializedName

// Estrutura principal da resposta da API de previsão
data class ForecastResponse(
    @SerializedName("forecast") val forecast: Forecast
)

data class Forecast(
    @SerializedName("forecastday") val forecastday: List<ForecastDay>
)

data class ForecastDay(
    @SerializedName("date") val date: String,
    @SerializedName("day") val day: Day
)

data class Day(
    @SerializedName("maxtemp_c") val maxtemp_c: Double,
    @SerializedName("mintemp_c") val mintemp_c: Double,
    @SerializedName("condition") val condition: Condition,
    @SerializedName("daily_chance_of_rain") val daily_chance_of_rain: Int // 👈 ADICIONE ESTA LINHA
)