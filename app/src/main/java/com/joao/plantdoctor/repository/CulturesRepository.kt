package com.joao.plantdoctor.repository

import android.util.Log
import com.joao.plantdoctor.models.AtividadeHistorico
import com.joao.plantdoctor.network.HistoryEventRequest
import com.joao.plantdoctor.network.PlantedCultureRequest
import com.joao.plantdoctor.network.RetrofitClient

class CulturesRepository {

    // Usando o serviço da sua API Plant Doctor
    private val apiService = RetrofitClient.plantDoctorApiService

    suspend fun addPlantedCulture(token: String, request: PlantedCultureRequest): Boolean {
        try {
            val response = apiService.addPlantedCulture("Bearer $token", request)
            if (response.isSuccessful) {
                Log.d("CulturesRepository", "Plantio salvo com sucesso.")
                return true
            } else {
                Log.e("CulturesRepository", "Erro ao salvar plantio: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e("CulturesRepository", "Exceção ao salvar plantio", e)
        }
        return false
    }

    suspend fun addHistoryEvent(token: String, plantedCultureId: Int, request: HistoryEventRequest): AtividadeHistorico? {
        try {
            val response = apiService.addHistoryEvent("Bearer $token", plantedCultureId, request)
            if (response.isSuccessful) {
                Log.d("CulturesRepository", "Evento de histórico salvo com sucesso.")
                return response.body()
            } else {
                Log.e("CulturesRepository", "Erro ao salvar evento de histórico: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e("CulturesRepository", "Exceção ao salvar evento de histórico", e)
        }
        return null
    }
}