package com.joao.PlantSoS.models

import java.util.Date

// Este é o "molde" dos dados que o seu ViewModel vai carregar do banco
// e o seu Adapter vai usar para desenhar a tela.
data class DiagnosisHistoryItem(
    val id: Int, // O ID do diagnóstico no banco de dados
    val cultureId: Int,
    val cultureName: String, // O nome da cultura (ex: "Milho")
    val diagnosisName: String, // O resultado da IA (ex: "Míldio")
    val observation: String,   // A observação (ex: "IA detectou com 95%...")
    val photoPath: String,     // A URI da foto salva (como String)
    val analysisDate: Date     // A data/hora da análise
)