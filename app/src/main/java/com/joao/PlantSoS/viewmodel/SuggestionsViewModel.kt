package com.joao.PlantSoS.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.joao.PlantSoS.models.Suggestion
import com.joao.PlantSoS.models.SuggestionRequest
import com.joao.PlantSoS.network.*
import kotlinx.coroutines.launch

class SuggestionsViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService: PlantDoctorApiService = RetrofitClient.plantDoctorApiService
    private val sharedPrefs = application.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)

    private val _suggestionsList = MutableLiveData<Result<List<Suggestion>>>()
    val suggestionsList: LiveData<Result<List<Suggestion>>> get() = _suggestionsList

    private val _postResult = MutableLiveData<Result<Suggestion>>()
    val postResult: LiveData<Result<Suggestion>> get() = _postResult

    private fun getToken(): String? {
        val token = sharedPrefs.getString("AUTH_TOKEN", null)
        return if (token != null) "Bearer $token" else null
    }

    fun fetchSuggestions() {
        viewModelScope.launch {
            val token = getToken()
            if (token == null) {
                _suggestionsList.postValue(Result.failure(Exception("Usuário não autenticado.")))
                return@launch
            }
            try {
                val response = apiService.getSuggestions(token)
                if (response.isSuccessful && response.body() != null) {
                    _suggestionsList.postValue(Result.success(response.body()!!))
                } else {
                    _suggestionsList.postValue(Result.failure(Exception("Erro ao buscar sugestões.")))
                }
            } catch (e: Exception) {
                _suggestionsList.postValue(Result.failure(e))
            }
        }
    }

    fun postSuggestion(suggestion: String) {
        viewModelScope.launch {
            val token = getToken()
            if (token == null) {
                _postResult.postValue(Result.failure(Exception("Usuário não autenticado.")))
                return@launch
            }
            try {
                val request = SuggestionRequest(suggestionText = suggestion)
                val response = apiService.postSuggestion(token, request)
                if (response.isSuccessful) {
                    _postResult.postValue(Result.success(response.body()!!))
                    fetchSuggestions()
                } else {
                    _postResult.postValue(Result.failure(Exception("Erro ao enviar sugestão.")))
                }
            } catch (e: Exception) {
                _postResult.postValue(Result.failure(e))
            }
        }
    }
}