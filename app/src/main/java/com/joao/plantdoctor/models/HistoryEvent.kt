package com.joao.plantdoctor.models

import java.util.Date

/**
 * Representa um único evento no histórico de uma cultura plantada.
 */
data class HistoryEvent(
    val date: Date,
    val eventType: EventType,
    val observation: String
)

/**
 * Enum para categorizar os tipos de eventos do histórico.
 */
enum class EventType {
    PLANTIO,       // Planting
    ADUBAGEM,      // Fertilization
    AGROTOXICO,    // Agrochemical
    VENENO,        // Pesticide/Poison
    COLHEITA,      // Harvest
    OUTRO          // Other
}