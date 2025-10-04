package com.joao.plantdoctor.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.joao.plantdoctor.models.User
import com.joao.plantdoctor.network.PlantDoctorApiService
import com.joao.plantdoctor.network.RetrofitClient
import kotlinx.coroutines.launch

class ManageUsersViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService: PlantDoctorApiService = RetrofitClient.plantDoctorApiService
    private val sharedPrefs = application.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)

    private val _usersList = MutableLiveData<Result<List<User>>>()
    val usersList: LiveData<Result<List<User>>> get() = _usersList

    private fun getToken(): String? {
        val token = sharedPrefs.getString("AUTH_TOKEN", null)
        return if (token != null) "Bearer $token" else null
    }

    fun fetchAllUsers() {
        viewModelScope.launch {
            val token = getToken()
            if (token == null) {
                _usersList.postValue(Result.failure(Exception("Admin não autenticado.")))
                return@launch
            }
            try {
                val response = apiService.getAllUsers(token)
                if (response.isSuccessful && response.body() != null) {
                    _usersList.postValue(Result.success(response.body()!!))
                } else {
                    _usersList.postValue(Result.failure(Exception("Erro ao buscar usuários.")))
                }
            } catch (e: Exception) {
                _usersList.postValue(Result.failure(e))
            }
        }
    }
}