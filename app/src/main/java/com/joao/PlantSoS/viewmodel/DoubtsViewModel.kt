package com.joao.PlantSoS.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.joao.PlantSoS.models.Doubt
import com.joao.PlantSoS.network.DoubtRequest
import com.joao.PlantSoS.network.PlantDoctorApiService
import com.joao.PlantSoS.network.RetrofitClient
import kotlinx.coroutines.launch

class DoubtsViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService: PlantDoctorApiService = RetrofitClient.plantDoctorApiService
    private val sharedPrefs = application.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)

    private val _doubtsList = MutableLiveData<Result<List<Doubt>>>()
    val doubtsList: LiveData<Result<List<Doubt>>> get() = _doubtsList

    private val _postResult = MutableLiveData<Result<Doubt>>()
    val postResult: LiveData<Result<Doubt>> get() = _postResult

    private fun getToken(): String? {
        val token = sharedPrefs.getString("AUTH_TOKEN", null)
        return if (token != null) "Bearer $token" else null
    }

    fun fetchDoubts() {
        viewModelScope.launch {
            val token = getToken()
            if (token == null) {
                _doubtsList.postValue(Result.failure(Exception("Usuário não autenticado.")))
                return@launch
            }
            try {
                val response = apiService.getDoubts(token)
                if (response.isSuccessful && response.body() != null) {
                    _doubtsList.postValue(Result.success(response.body()!!))
                } else {
                    _doubtsList.postValue(Result.failure(Exception("Erro ao buscar dúvidas: ${response.code()}")))
                }
            } catch (e: Exception) {
                _doubtsList.postValue(Result.failure(e))
            }
        }
    }

    fun postDoubt(question: String) {
        viewModelScope.launch {
            val token = getToken()
            if (token == null) {
                _postResult.postValue(Result.failure(Exception("Usuário não autenticado.")))
                return@launch
            }
            try {
                // ▼▼▼ CORREÇÃO APLICADA AQUI ▼▼▼
                val request = DoubtRequest(questionText = question)
                val response = apiService.postDoubt(token, request)

                if (response.isSuccessful && response.body() != null) {
                    _postResult.postValue(Result.success(response.body()!!))
                    fetchDoubts() // Atualiza a lista automaticamente após o sucesso
                } else {
                    _postResult.postValue(Result.failure(Exception("Erro ao enviar dúvida: ${response.code()}")))
                }
            } catch (e: Exception) {
                _postResult.postValue(Result.failure(e))
            }
        }
    }
}