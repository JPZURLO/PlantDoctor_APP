package com.joao.PlantSoS

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joao.PlantSoS.models.ApiResponse
import com.joao.PlantSoS.models.LoginRequest
import com.joao.PlantSoS.models.LoginResponse
import com.joao.PlantSoS.models.ResetPasswordRequest
import com.joao.PlantSoS.models.UserRequest
import com.joao.PlantSoS.network.RetrofitClient
import kotlinx.coroutines.launch
import org.json.JSONObject // Importar a classe JSONObject

/**
 * ViewModel unificado para lidar com toda a lógica de autenticação:
 * Registo, Login e Recuperação de Senha.
 */
class AuthViewModel : ViewModel() {

    private val apiService = RetrofitClient.plantDoctorApiService

    // --- LiveData para o Registo ---
    private val _registerResult = MutableLiveData<Result<ApiResponse>>()
    val registerResult: LiveData<Result<ApiResponse>> get() = _registerResult

    // --- LiveData para o Login ---
    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> get() = _loginResult

    // --- LiveData para a Recuperação de Senha ---
    private val _requestResetResult = MutableLiveData<Result<ApiResponse>>()
    val requestResetResult: LiveData<Result<ApiResponse>> get() = _requestResetResult

    private val _resetPasswordResult = MutableLiveData<Result<ApiResponse>>()
    val resetPasswordResult: LiveData<Result<ApiResponse>> get() = _resetPasswordResult

    /**
     * ✅ NOVO: Função auxiliar para extrair a mensagem de erro do corpo JSON.
     * Isto evita que o JSON completo seja mostrado ao utilizador.
     */
    private fun parseErrorMessage(errorBody: String?): String {
        return try {
            val jsonObj = JSONObject(errorBody!!)
            jsonObj.getString("message")
        } catch (e: Exception) {
            // Se falhar a extração, retorna o corpo do erro original ou uma mensagem padrão.
            errorBody ?: "Ocorreu um erro desconhecido."
        }
    }

    /**
     * Solicita o envio de um e-mail de recuperação de senha.
     */
    fun requestPasswordReset(email: String) {
        viewModelScope.launch {
            try {
                val response = apiService.requestPasswordReset(email)
                if (response.isSuccessful && response.body() != null) {
                    _requestResetResult.postValue(Result.success(response.body()!!))
                } else {
                    val errorMessage = parseErrorMessage(response.errorBody()?.string())
                    _requestResetResult.postValue(Result.failure(Exception(errorMessage)))
                }
            } catch (e: Exception) {
                _requestResetResult.postValue(Result.failure(Exception("Falha na ligação: ${e.message}")))
            }
        }
    }

    /**
     * Define uma nova senha usando um token de redefinição.
     */
    fun resetPassword(request: ResetPasswordRequest) {
        viewModelScope.launch {
            try {
                val response = apiService.resetPassword(request)
                if (response.isSuccessful && response.body() != null) {
                    _resetPasswordResult.postValue(Result.success(response.body()!!))
                } else {
                    val errorMessage = parseErrorMessage(response.errorBody()?.string())
                    _resetPasswordResult.postValue(Result.failure(Exception(errorMessage)))
                }
            } catch (e: Exception) {
                _resetPasswordResult.postValue(Result.failure(Exception("Falha na ligação: ${e.message}")))
            }
        }
    }

    fun loginUser(loginData: LoginRequest) {
        viewModelScope.launch {
            try {
                val response = apiService.login(loginData)
                if (response.isSuccessful && response.body() != null) {
                    _loginResult.postValue(Result.success(response.body()!!))
                } else {
                    // ✅ CORRIGIDO: Agora extrai a mensagem de erro do JSON.
                    val errorMessage = parseErrorMessage(response.errorBody()?.string())
                    _loginResult.postValue(Result.failure(Exception(errorMessage)))
                }
            } catch (e: Exception) {
                _loginResult.postValue(Result.failure(e))
            }
        }
    }

    fun registerUser(userData: UserRequest) {
        viewModelScope.launch {
            try {
                val response = apiService.register(userData)
                if (response.isSuccessful && response.body() != null) {
                    _registerResult.postValue(Result.success(response.body()!!))
                } else {
                    // ✅ CORRIGIDO: Agora extrai a mensagem de erro do JSON.
                    val errorMessage = parseErrorMessage(response.errorBody()?.string())
                    _registerResult.postValue(Result.failure(Exception(errorMessage)))
                }
            } catch (e: Exception) {
                _registerResult.postValue(Result.failure(Exception("Falha na ligação: ${e.message}")))
            }
        }
    }
}
