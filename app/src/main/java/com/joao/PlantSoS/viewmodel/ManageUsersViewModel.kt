package com.joao.PlantSoS.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.joao.PlantSoS.models.User
import com.joao.PlantSoS.network.PlantDoctorApiService
import com.joao.PlantSoS.network.RetrofitClient
import com.joao.PlantSoS.network.UserUpdateRequest
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

    fun updateUser(userId: Int, name: String, email: String, password: String?, role: String) {
        viewModelScope.launch {
            val token = getToken()
            if (token == null) {
                // TODO: Lidar com erro de token
                return@launch
            }
            try {
                val request = UserUpdateRequest(name, email, password, role)
                val response = apiService.updateUser(token, userId, request)
                if (response.isSuccessful) {
                    // Sucesso! A lista será atualizada na próxima vez que a tela for aberta
                    // ou podemos forçar a atualização aqui.
                    fetchAllUsers()
                    // TODO: Postar um resultado de sucesso para a UI
                } else {
                    // TODO: Postar um resultado de falha para a UI
                }
            } catch (e: Exception) {
                // TODO: Lidar com exceção
            }
        }
    }
}