package com.joao.PlantSoS.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.joao.PlantSoS.models.ApiResponse
import com.joao.PlantSoS.models.Culture
import com.joao.PlantSoS.models.SaveCulturesRequest
import com.joao.PlantSoS.network.PlantDoctorApiService
import com.joao.PlantSoS.network.RetrofitClient
import kotlinx.coroutines.launch

/**
 * Este ViewModel cuida APENAS das listas de culturas (Todas as Culturas, Minhas Culturas).
 * É usado por 'MyCulturesFragment' e 'SelectCultureDialogFragment'.
 */
class CultureViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService: PlantDoctorApiService = RetrofitClient.plantDoctorApiService
    private val sharedPrefs = application.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)

    // LiveData para a lista de todas as culturas disponíveis
    private val _allCultures = MutableLiveData<Result<List<Culture>>>()
    val allCultures: LiveData<Result<List<Culture>>> get() = _allCultures

    // LiveData para a lista de culturas do utilizador
    private val _userCultures = MutableLiveData<Result<List<Culture>>>()
    val userCultures: LiveData<Result<List<Culture>>> get() = _userCultures

    // LiveData para o resultado da operação de guardar
    private val _saveResult = MutableLiveData<Result<ApiResponse>>()
    val saveResult: LiveData<Result<ApiResponse>> get() = _saveResult

    private fun getToken(): String? {
        return sharedPrefs.getString("AUTH_TOKEN", null)
    }

    fun fetchAllCultures() {
        viewModelScope.launch {
            val token = getToken()
            if (token == null) {
                _allCultures.postValue(Result.failure(Exception("Utilizador não autenticado.")))
                return@launch
            }
            try {
                val response = apiService.getAllCultures("Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    _allCultures.postValue(Result.success(response.body()!!))
                } else {
                    _allCultures.postValue(Result.failure(Exception("Erro ao buscar culturas: ${response.code()}")))
                }
            } catch (e: Exception) {
                _allCultures.postValue(Result.failure(e))
            }
        }
    }

    fun fetchUserCultures() {
        Log.d("CultureViewModel_Debug", "Iniciando fetchUserCultures...")
        viewModelScope.launch {
            val token = getToken()
            Log.d("CultureViewModel_Debug", "Token encontrado: $token")
            if (token == null) {
                Log.e("CultureViewModel_Debug", "TOKEN É NULO! Abortando a busca.")
                _userCultures.postValue(Result.failure(Exception("Utilizador não autenticado.")))
                return@launch
            }
            try {
                Log.d("CultureViewModel_Debug", "Tentando chamar a API getMyCultures...")
                val response = apiService.getMyCultures("Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    val cultureCount = response.body()!!.size
                    Log.d("CultureViewModel_Debug", "API retornou sucesso com $cultureCount culturas.")
                    _userCultures.postValue(Result.success(response.body()!!))
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("CultureViewModel_Debug", "API retornou erro. Código: ${response.code()}, Mensagem: $errorBody")
                    _userCultures.postValue(Result.failure(Exception("Erro ao buscar as suas culturas: ${response.code()}")))
                }
            } catch (e: Exception) {
                Log.e("CultureViewModel_Debug", "Ocorreu uma exceção na chamada à API", e)
                _userCultures.postValue(Result.failure(e))
            }
        }
    }

    fun saveUserCultures(cultureIds: List<Int>) {
        viewModelScope.launch {
            val token = getToken()
            if (token == null) {
                _saveResult.postValue(Result.failure(Exception("Utilizador não autenticado.")))
                return@launch
            }
            try {
                val requestBody = SaveCulturesRequest(cultureIds = cultureIds)
                val response = apiService.saveUserCultures("Bearer $token", requestBody)
                if (response.isSuccessful && response.body() != null) {
                    _saveResult.postValue(Result.success(response.body()!!))
                } else {
                    _saveResult.postValue(Result.failure(Exception("Erro ao guardar culturas: ${response.code()}")))
                }
            } catch (e: Exception) {
                _saveResult.postValue(Result.failure(e))
            }
        }
    }
}
