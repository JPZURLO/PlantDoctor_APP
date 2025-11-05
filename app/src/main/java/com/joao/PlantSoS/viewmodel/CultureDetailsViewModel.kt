package com.joao.PlantSoS.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joao.PlantSoS.models.AtividadeHistorico
import com.joao.PlantSoS.models.PlantedCulture
import com.joao.PlantSoS.models.DiagnosisHistoryItem
import com.joao.PlantSoS.models.DiagnosisRequest  // ✅ CORRETO: caminho certo da data class
import com.joao.PlantSoS.models.DiseaseExplanationResponse
import com.joao.PlantSoS.models.HistoryEventRequest
import com.joao.PlantSoS.models.PlantedCultureRequest
import com.joao.PlantSoS.network.RetrofitClient
import com.joao.PlantSoS.repository.CulturesRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CultureDetailsViewModel : ViewModel() {

    private val culturesRepository = CulturesRepository()

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

    // --- ✅ DIAGNÓSTICOS ---
    private val _diagnosisHistory = MutableLiveData<List<DiagnosisHistoryItem>>()
    val diagnosisHistory: LiveData<List<DiagnosisHistoryItem>> get() = _diagnosisHistory

    private val _diagnosisSaveResult = MutableLiveData<Result<Boolean>>()
    val diagnosisSaveResult: LiveData<Result<Boolean>> get() = _diagnosisSaveResult
    // ----------------------

    fun clearState() {
        _plantedCultureDetail.value = null
        _allPlantings.value = emptyList()
        _historyList.value = emptyList()
        _diagnosisHistory.value = emptyList()
    }

    fun fetchAllCultureData(token: String, cultureId: Int) {
        viewModelScope.launch {
            val allPlantings = culturesRepository.getAllPlantingsForCulture(token, cultureId)
            _allPlantings.postValue(allPlantings ?: emptyList())

            val mostRecentPlanting = allPlantings?.firstOrNull()
            _plantedCultureDetail.postValue(mostRecentPlanting)

            if (mostRecentPlanting != null) {
                val history = culturesRepository.getHistoryForPlantedCulture(token, mostRecentPlanting.id)
                _historyList.postValue(history ?: emptyList())
            } else {
                _historyList.postValue(emptyList())
            }

            fetchDiagnosisHistory(token, cultureId)
        }
    }

    private fun fetchDiagnosisHistory(token: String, cultureId: Int) {
        viewModelScope.launch {
            try {
                // Agora "response" é um objeto Response<>
                // ⬇️ CORREÇÃO AQUI ⬇️
                val response = culturesRepository.getDiagnosisHistory(token, cultureId)
                if (response.isSuccessful) { // .isSuccessful
                    _diagnosisHistory.postValue(response.body() ?: emptyList()) // .body()
                } else {
                    Log.e("CultureDetailsVM", "Erro: ${response.code()}") // .code() NÃO é .code()
                }
            } catch (e: Exception) {
                _diagnosisHistory.postValue(emptyList())
            }
        }
    }

    fun saveNewDiagnosis(
        token: String,
        cultureId: Int,
        diagnosisName: String,
        observation: String,
        photoPath: String,
        analysisDate: Date
    ) {
        viewModelScope.launch {
            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                val analysisDateStr = sdf.format(analysisDate)

                val request = DiagnosisRequest(
                    culture_id = cultureId,
                    diagnosis_name = diagnosisName,
                    observation = observation,
                    photo_path = photoPath
                )

                val response = RetrofitClient.plantDoctorApiService.saveDiagnosis("Bearer $token", request)

                if (response.isSuccessful) {
                    _diagnosisSaveResult.postValue(Result.success(true))
                    fetchDiagnosisHistory(token, cultureId) // ✅ Atualiza lista automaticamente
                } else {
                    _diagnosisSaveResult.postValue(Result.failure(Exception("Erro HTTP: ${response.code()}")))
                }
            } catch (e: Exception) {
                Log.e("CultureDetailsVM", "Erro ao salvar diagnóstico", e)
                _diagnosisSaveResult.postValue(Result.failure(e))
            }
        }
    }

    fun saveNewPlanting(token: String, cultureId: Int, plantingDate: String, notes: String?) {
        viewModelScope.launch {
            val request = PlantedCultureRequest(culture_id = cultureId, planting_date = plantingDate, notes = notes)
            val result = culturesRepository.addPlantedCulture(token, request)

            if (result != null) {
                _newlyPlantedCulture.postValue(Result.success(result))
                fetchAllCultureData(token, cultureId)
            } else {
                _newlyPlantedCulture.postValue(Result.failure(Exception("Falha ao criar o plantio.")))
            }
        }
    }

    fun saveHistoryEvent(token: String, plantedCultureId: Int, eventType: String, observation: String) {
        viewModelScope.launch {
            val request = HistoryEventRequest(event_type = eventType, observation = observation)
            val savedEvent = culturesRepository.addHistoryEvent(token, plantedCultureId, request)

            if (savedEvent != null) {
                val currentList = _historyList.value?.toMutableList() ?: mutableListOf()
                currentList.add(0, savedEvent)
                _historyList.postValue(currentList)
                _saveResult.postValue(Result.success(savedEvent))
            } else {
                _saveResult.postValue(Result.failure(Exception("Erro ao salvar evento.")))
            }
        }
    }

    private val _diseaseExplanation = MutableLiveData<Result<DiseaseExplanationResponse>>()
    val diseaseExplanation: LiveData<Result<DiseaseExplanationResponse>> get() = _diseaseExplanation

    // ✅ ADICIONE ESTA FUNÇÃO
    fun fetchDiseaseExplanation(diseaseName: String) {
        viewModelScope.launch {
            try {
                val response = culturesRepository.getDiseaseExplanation(diseaseName)

                if (response.isSuccessful && response.body() != null) {
                    _diseaseExplanation.postValue(Result.success(response.body()!!))
                } else {
                    // Tenta ler a "mensagem" (ex: "Não é uma planta")
                    val errorBody = response.errorBody()?.string()
                    val errorMsg = try {
                        org.json.JSONObject(errorBody!!).getString("mensagem")
                    } catch (e: Exception) {
                        "Explicação não encontrada."
                    }
                    _diseaseExplanation.postValue(Result.failure(Exception(errorMsg)))
                }
            } catch (e: Exception) {
                _diseaseExplanation.postValue(Result.failure(e))
            }
        }
    }
}
