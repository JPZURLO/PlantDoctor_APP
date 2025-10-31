package com.joao.PlantSoS.models

import com.google.gson.annotations.SerializedName

// Estrutura principal da resposta da API de previsão (Inalterada)
data class ForecastResponse(
    @SerializedName("forecast") val forecast: Forecast
)

data class Forecast(
    @SerializedName("forecastday") val forecastday: List<ForecastDay>
)

data class ForecastDay(
    @SerializedName("date") val date: String,
    @SerializedName("day") val day: Day // Inalterado
)

// A classe Condition também precisa existir para o campo de ícone, mas não está mostrada.
// Deve ser algo como:
/*
data class Condition(
    @SerializedName("text") val text: String,
    @SerializedName("icon") val icon: String
)
*/

// ✅ CORRIGIDO: Esta classe agora inclui Umidade e Chance de Chuva Diária
data class Day(
    // ✅ CORREÇÃO TEMPERATURA: Mapeamento de Snake_Case para CamelCase
    @SerializedName("maxtemp_c")
    val maxtempC: Double,

    @SerializedName("mintemp_c")
    val mintempC: Double,

    // Condição
    @SerializedName("condition")
    val condition: Condition,

    // Umidade e Chuva (Correções anteriores)
    @SerializedName("avghumidity")
    val avgHumidity: Double,

    @SerializedName("daily_chance_of_rain")
    val dailyChanceOfRain: Int
)