package com.joao.plantdoctor.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.joao.plantdoctor.network.*
import kotlinx.coroutines.launch

class RankingViewModel(application: Application) : AndroidViewModel(application) {
    private val apiService: PlantDoctorApiService = RetrofitClient.plantDoctorApiService
    private val sharedPrefs = application.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)

    private val _rankingData = MutableLiveData<Result<List<CultureRankingItem>>>()
    val rankingData: LiveData<Result<List<CultureRankingItem>>> get() = _rankingData

    private fun getToken(): String? {
        val token = sharedPrefs.getString("AUTH_TOKEN", null)
        return if (token != null) "Bearer $token" else null
    }

    fun fetchRanking() {
        viewModelScope.launch {
            val token = getToken()
            if (token == null) {
                _rankingData.postValue(Result.failure(Exception("Usuário não autenticado.")))
                return@launch
            }
            try {
                val response = apiService.getCultureRanking(token)
                if (response.isSuccessful && response.body() != null) {
                    _rankingData.postValue(Result.success(response.body()!!))
                } else {
                    _rankingData.postValue(Result.failure(Exception("Erro ao buscar ranking.")))
                }
            } catch (e: Exception) {
                _rankingData.postValue(Result.failure(e))
            }
        }
    }
}