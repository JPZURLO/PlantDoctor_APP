package com.joao.plantdoctor

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.joao.plantdoctor.models.Culture
import com.joao.plantdoctor.network.RetrofitClient
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val _myCultures = MutableLiveData<Result<List<Culture>>>()
    val myCultures: LiveData<Result<List<Culture>>> get() = _myCultures

    private val apiService = RetrofitClient.plantDoctorApiService
    private val sharedPrefs = application.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)

    fun fetchMyCultures() {
        viewModelScope.launch {
            val token = sharedPrefs.getString("AUTH_TOKEN", null)
            if (token == null) {
                _myCultures.postValue(Result.failure(Exception("Token de autenticação não encontrado.")))
                return@launch
            }

            try {
                val response = apiService.getMyCultures("Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    _myCultures.postValue(Result.success(response.body()!!))
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Erro ao buscar culturas."
                    _myCultures.postValue(Result.failure(Exception(errorMsg)))
                }
            } catch (e: Exception) {
                _myCultures.postValue(Result.failure(Exception("Falha na ligação: ${e.message}")))
            }
        }
    }
}
