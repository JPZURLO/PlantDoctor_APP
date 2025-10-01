package com.joao.plantdoctor.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joao.plantdoctor.models.AtividadeHistorico
import com.joao.plantdoctor.models.PlantedCulture
import com.joao.plantdoctor.network.HistoryEventRequest
import com.joao.plantdoctor.network.PlantedCultureRequest
import com.joao.plantdoctor.repository.CulturesRepository
import kotlinx.coroutines.launch

class CultureDetailsViewModel : ViewModel() {

    private val culturesRepository = CulturesRepository()

    // NOVO: LiveData para a lista completa de plantios da cultura (ordenada)
    private val _allPlantings = MutableLiveData<List<PlantedCulture>>()
    val allPlantings: LiveData<List<PlantedCulture>> get() = _allPlantings

    private val _plantedCultureDetail = MutableLiveData<PlantedCulture?>()
    val plantedCultureDetail: LiveData<PlantedCulture?> get() = _plantedCultureDetail

    private val _newlyPlantedCulture = MutableLiveData<Result<PlantedCulture>>()
    val newlyPlantedCulture: LiveData<Result<PlantedCulture>> get() = _newlyPlantedCulture

    private val _historyList = MutableLiveData<List<AtividadeHistorico>>()
    val historyList: LiveData<List<AtividadeHistorico>> get() = _historyList

    private val _saveResult = MutableLiveData<Result<AtividadeHistorico>>()
    val saveResult: LiveData<Result<AtividadeHistorico>> get() = _saveResult

    fun clearState() {
        _plantedCultureDetail.value = null
        _allPlantings.value = emptyList() // NOVO: Limpa a lista completa
        _historyList.value = emptyList()
    }

    // ATUALIZADO: Função de busca agora busca a lista completa
    fun fetchAllCulturePlantings(token: String, cultureId: Int) {
        viewModelScope.launch {
            val allPlantings = culturesRepository.getAllPlantingsForCulture(token, cultureId)
            _allPlantings.postValue(allPlantings ?: emptyList()) // Posta a lista completa

            val mostRecentPlanting = allPlantings?.firstOrNull() // O mais recente é o primeiro item
            _plantedCultureDetail.postValue(mostRecentPlanting)

            if (mostRecentPlanting != null) {
                val history = culturesRepository.getHistoryForPlantedCulture(token, mostRecentPlanting.id)
                _historyList.postValue(history ?: emptyList())
            } else {
                _historyList.postValue(emptyList())
            }
        }
    }

    fun saveNewPlanting(token: String, cultureId: Int, plantingDate: String, notes: String?) {
        viewModelScope.launch {
            val request = PlantedCultureRequest(culture_id = cultureId, planting_date = plantingDate, notes = notes)
            val result: PlantedCulture? = culturesRepository.addPlantedCulture(token, request)

            if (result != null) {
                _newlyPlantedCulture.postValue(Result.success(result))

                // CRÍTICO: Chama a função de busca COMPLETA para atualizar TODA a lista e o estado
                fetchAllCulturePlantings(token, cultureId)

            } else {
                _newlyPlantedCulture.postValue(Result.failure(Exception("Falha ao criar o plantio na API.")))
            }
        }
    }

    fun saveHistoryEvent(token: String, plantedCultureId: Int, eventType: String, observation: String) {
        viewModelScope.launch {
            val request = HistoryEventRequest(event_type = eventType, observation = observation)
            val savedEvent: AtividadeHistorico? = culturesRepository.addHistoryEvent(token, plantedCultureId, request)

            if (savedEvent != null) {
                Log.d("ViewModel", "Evento salvo! Descrição: ${savedEvent.observation}")
                val currentList = _historyList.value?.toMutableList() ?: mutableListOf()
                currentList.add(0, savedEvent)
                _historyList.postValue(currentList)
                _saveResult.postValue(Result.success(savedEvent))
            } else {
                Log.e("ViewModel", "Falha ao salvar evento de histórico.")
                _saveResult.postValue(Result.failure(Exception("Não foi possível salvar o evento de histórico.")))
            }
        }
    }
}