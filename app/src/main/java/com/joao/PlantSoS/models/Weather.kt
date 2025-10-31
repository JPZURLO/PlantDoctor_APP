package com.joao.PlantSoS.models

import com.google.gson.annotations.SerializedName

// Classe para modelar a resposta completa da API
data class WeatherResponse(
    @SerializedName("location") val location: Location,
    @SerializedName("current") val current: Current
)

// Classe para os dados de localização
data class Location(
    @SerializedName("name") val name: String,
    @SerializedName("region") val region: String,
    @SerializedName("country") val country: String
)

// Classe para os dados do tempo atual
data class Current(
    @SerializedName("temp_c") val tempC: Double,
    @SerializedName("condition") val condition: Condition,
    @SerializedName("wind_kph") val windKph: Double,
    @SerializedName("humidity") val humidity: Int,
    @SerializedName("feelslike_c") val feelslikeC: Double
)

// Classe para a condição do tempo (texto e ícone)
data class Condition(
    @SerializedName("text") val text: String,
    @SerializedName("icon") val icon: String
)