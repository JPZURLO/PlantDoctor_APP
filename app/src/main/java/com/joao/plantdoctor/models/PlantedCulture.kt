package com.joao.plantdoctor.models

import java.util.Date

/**
 * Representa uma instância específica de uma cultura plantada pelo usuário.
 */
data class PlantedCulture(
    val id: String, // Um ID único para este plantio, pode ser gerado aleatoriamente
    val cultureId: Int, // O ID da cultura original (ex: Milho, Soja)
    val plantingDate: Date,
    val predictedHarvestDate: Date,
    val history: MutableList<HistoryEvent> = mutableListOf()
)