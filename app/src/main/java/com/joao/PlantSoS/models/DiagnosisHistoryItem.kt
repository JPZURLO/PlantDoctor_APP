package com.joao.PlantSoS.models

import java.util.Date


// Corrige 19 erros no DiagnosisHistoryAdapter e CultureDetailsViewModel
data class DiagnosisHistoryItem(
    val id: Int,
    val diagnosis_name: String,
    val observation: String?,
    val analysis_date: String,
    val photo_path: String
)