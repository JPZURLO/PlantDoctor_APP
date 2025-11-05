package com.joao.PlantSoS.models
// (ou com.joao.PlantSoS.models)

data class PlantedCultureRequest(
    val culture_id: Int,
    val planting_date: String,
    val notes: String?
)