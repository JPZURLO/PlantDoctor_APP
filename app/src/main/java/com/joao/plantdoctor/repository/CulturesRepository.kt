package com.joao.plantdoctor.repository

import android.util.Log
import com.joao.plantdoctor.models.AtividadeHistorico
import com.joao.plantdoctor.models.PlantedCulture
import com.joao.plantdoctor.network.HistoryEventRequest
import com.joao.plantdoctor.network.PlantedCultureRequest
import com.joao.plantdoctor.network.RetrofitClient
import java.text.SimpleDateFormat
import java.util.*

class CulturesRepository {

    private val apiService = RetrofitClient.plantDoctorApiService
    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // FUNÇÕES DE ESCRITA (Inalteradas)
    suspend fun addPlantedCulture(token: String, request: PlantedCultureRequest): PlantedCulture? {
        try {
            val response = apiService.addPlantedCulture("Bearer $token", request)
            if (response.isSuccessful) {
                Log.d("CulturesRepository", "Plantio salvo com sucesso.")
                return response.body()
            } else {
                Log.e("CulturesRepository", "Erro ao salvar plantio: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e("CulturesRepository", "Exceção ao salvar plantio", e)
        }
        return null
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

    // FUNÇÃO DE LEITURA (Base para todas as buscas)
    suspend fun getPlantedCultures(token: String): List<PlantedCulture>? {
        try {
            val response = apiService.getPlantedCultures("Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                Log.d("CulturesRepository", "Lista de plantios buscada com sucesso.")
                return response.body()
            } else {
                Log.e("CulturesRepository", "Erro ao buscar plantios: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e("CulturesRepository", "Exceção ao buscar plantios", e)
        }
        return null
    }

    // NOVO: Busca TODOS os plantios de uma cultura, ordenados do mais recente ao mais antigo
    suspend fun getAllPlantingsForCulture(token: String, cultureId: Int): List<PlantedCulture>? {
        val allPlantings = getPlantedCultures(token)

        // Filtra pela CultureId e ordena pela data de plantio (mais recente primeiro)
        return allPlantings
            ?.filter { it.culture.id == cultureId }
            ?.sortedByDescending {
                try {
                    sdf.parse(it.planting_date)?.time ?: 0L
                } catch (e: Exception) {
                    0L
                }
            }
    }

    // Simplificada: Obtém o histórico de um plantio específico
    suspend fun getHistoryForPlantedCulture(token: String, plantedCultureId: Int): List<AtividadeHistorico>? {
        val allPlantings = getPlantedCultures(token)

        return allPlantings
            ?.find { it.id == plantedCultureId }
            ?.history_events
    }

    // FUNÇÃO ANTIGA REMOVIDA: getMostRecentPlantedCulture não é mais necessária, pois é feita na função acima.
}