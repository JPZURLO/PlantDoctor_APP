package com.joao.PlantSoS.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.joao.PlantSoS.models.UserHistoryItem
import com.joao.PlantSoS.network.*
import kotlinx.coroutines.launch

class UserHistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService: PlantDoctorApiService = RetrofitClient.plantDoctorApiService
    private val sharedPrefs = application.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)

    private val _historyList = MutableLiveData<Result<List<UserHistoryItem>>>()
    val historyList: LiveData<Result<List<UserHistoryItem>>> get() = _historyList

    private fun getToken(): String? {
        val token = sharedPrefs.getString("AUTH_TOKEN", null)
        return if (token != null) "Bearer $token" else null
    }

    fun fetchUserHistory(userId: Int) {
        viewModelScope.launch {
            val token = getToken()
            if (token == null) {
                _historyList.postValue(Result.failure(Exception("Admin não autenticado.")))
                return@launch
            }
            try {
                val response = apiService.getUserHistory(token, userId)
                if (response.isSuccessful && response.body() != null) {
                    _historyList.postValue(Result.success(response.body()!!))
                } else {
                    _historyList.postValue(Result.failure(Exception("Erro ao buscar histórico.")))
                }
            } catch (e: Exception) {
                _historyList.postValue(Result.failure(e))
            }
        }
    }
}