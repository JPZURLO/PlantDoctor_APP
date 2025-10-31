package com.joao.PlantSoS.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
// ✅ IMPORTS ADICIONADOS
import com.joao.PlantSoS.models.DiagnosisHistoryItem
import java.util.Date
// ---
import com.joao.PlantSoS.models.AtividadeHistorico
import com.joao.PlantSoS.models.PlantedCulture
import com.joao.PlantSoS.network.HistoryEventRequest
import com.joao.PlantSoS.network.PlantedCultureRequest
import com.joao.PlantSoS.repository.CulturesRepository
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

    // --- ✅ CÓDIGO DE DIAGNÓSTICO ADICIONADO ---
    private val _diagnosisHistory = MutableLiveData<List<DiagnosisHistoryItem>>()
    val diagnosisHistory: LiveData<List<DiagnosisHistoryItem>> get() = _diagnosisHistory

    private val _diagnosisSaveResult = MutableLiveData<Result<Boolean>>()
    val diagnosisSaveResult: LiveData<Result<Boolean>> get() = _diagnosisSaveResult
    // --- FIM DO CÓDIGO ADICIONADO ---

    fun clearState() {
        _plantedCultureDetail.value = null
        _allPlantings.value = emptyList() // NOVO: Limpa a lista completa
        _historyList.value = emptyList()
        _diagnosisHistory.value = emptyList() // ✅ LIMPA O NOVO HISTÓRICO
    }

    // ATUALIZADO: Função de busca agora busca a lista completa E OS DIAGNÓSTICOS
    fun fetchAllCultureData(token: String, cultureId: Int) {
        viewModelScope.launch {
            // 1. Busca plantios e histórico de atividades
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

            // 2. ✅ BUSCA O HISTÓRICO DE DIAGNÓSTICOS
            fetchDiagnosisHistory(token, cultureId)
        }
    }

    // --- ✅ NOVAS FUNÇÕES DE DIAGNÓSTICO ---

    /**
     * Busca o histórico de diagnósticos da IA para uma CULTURA
     */
    private fun fetchDiagnosisHistory(token: String, cultureId: Int) {
        viewModelScope.launch {
            try {
                // TODO: Chame seu repositório
                // val diagnosisList = culturesRepository.getDiagnosisHistory(token, cultureId)
                // _diagnosisHistory.postValue(diagnosisList ?: emptyList())
            } catch (e: Exception) {
                Log.e("CultureDetailsVM", "Erro ao buscar histórico de diagnósticos", e)
                _diagnosisHistory.postValue(emptyList())
            }
        }
    }

    /**
     * Salva um novo diagnóstico no banco de dados.
     * Chamado pelo DiagnoseFragment.
     */
    fun saveNewDiagnosis(
        token: String, // ✅ Token é necessário
        cultureId: Int?,
        diagnosisName: String,
        observation: String,
        photoPath: String,
        analysisDate: Date
    ) {
        viewModelScope.launch {
            if (cultureId == null) {
                _diagnosisSaveResult.postValue(Result.failure(Exception("ID da Cultura nulo")))
                return@launch
            }
            try {
                // TODO: Chame seu repositório
                // val request = DiagnosisSaveRequest(cultureId, diagnosisName, observation, photoPath, analysisDate)
                // val success = culturesRepository.saveNewDiagnosis(token, request)
                // if(success) {
                //    _diagnosisSaveResult.postValue(Result.success(true))
                // } else {
                //    _diagnosisSaveResult.postValue(Result.failure(Exception("API falhou ao salvar")))
                // }
                _diagnosisSaveResult.postValue(Result.success(true)) // Placeholder
            } catch (e: Exception) {
                _diagnosisSaveResult.postValue(Result.failure(e))
            }
        }
    }
    // --- FIM DAS NOVAS FUNÇÕES ---


    fun saveNewPlanting(token: String, cultureId: Int, plantingDate: String, notes: String?) {
        viewModelScope.launch {
            val request = PlantedCultureRequest(culture_id = cultureId, planting_date = plantingDate, notes = notes)
            val result: PlantedCulture? = culturesRepository.addPlantedCulture(token, request)

            if (result != null) {
                _newlyPlantedCulture.postValue(Result.success(result))
                // CRÍTICO: Chama a função de busca COMPLETA para atualizar TODA a lista e o estado
                fetchAllCultureData(token, cultureId) // ✅ ATUALIZADO PARA FUNÇÃO PRINCIPAL

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

