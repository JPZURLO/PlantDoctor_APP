package com.joao.PlantSoS.models

data class DiagnosisRequest(
    val culture_id: Int,
    val diagnosis_name: String,
    val observation: String?,
    val photo_path: String
)
