package com.joao.plantdoctor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joao.plantdoctor.models.AtividadeHistorico
import com.joao.plantdoctor.network.HistoryEventRequest
import com.joao.plantdoctor.network.PlantedCultureRequest
import com.joao.plantdoctor.repository.CulturesRepository
import kotlinx.coroutines.launch

class CultureDetailsViewModel : ViewModel() {

    private val culturesRepository = CulturesRepository()

    // Função para salvar um novo plantio
    fun saveNewPlanting(token: String, cultureId: Int, plantingDate: String, notes: String?) {
        // Usa o viewModelScope para rodar a chamada de rede em segundo plano de forma segura
        viewModelScope.launch {
            val request = PlantedCultureRequest(
                culture_id = cultureId,
                planting_date = plantingDate,
                notes = notes
            )
            culturesRepository.addPlantedCulture(token, request)
        }
    }

    // Função para adicionar um evento de histórico manual
    fun saveHistoryEvent(token: String, plantedCultureId: Int, eventType: String, observation: String) {
        viewModelScope.launch {
            val request = HistoryEventRequest(
                event_type = eventType,
                observation = observation
            )
            culturesRepository.addHistoryEvent(token, plantedCultureId, request)
        }
    }
}