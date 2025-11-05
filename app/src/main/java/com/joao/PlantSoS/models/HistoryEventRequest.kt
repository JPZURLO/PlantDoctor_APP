package com.joao.PlantSoS.models
// (ou com.joao.PlantSoS.models)

data class HistoryEventRequest(
    val event_type: String,
    val observation: String
)