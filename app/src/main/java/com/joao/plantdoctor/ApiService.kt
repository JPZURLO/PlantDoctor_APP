package com.joao.plantdoctor.network

import com.joao.plantdoctor.models.ApiResponse
import com.joao.plantdoctor.models.LoginRequest
import com.joao.plantdoctor.models.LoginResponse
import com.joao.plantdoctor.models.ResetPasswordRequest
import com.joao.plantdoctor.models.UserRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Interface que define os endpoints da API para o Retrofit.
 * Cada função corresponde a um pedido de rede.
 */
interface ApiService {

    @POST("/api/auth/register")
    suspend fun register(@Body userRequest: UserRequest): Response<ApiResponse>

    @POST("/api/auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    // ✅ CORRIGIDO: Adicionado o prefixo /api/auth/
    @GET("/api/auth/request-password-reset")
    suspend fun requestPasswordReset(@Query("email") email: String): Response<ApiResponse>

    // ✅ Esta já estava correta e consistente
    @POST("/api/auth/reset-password")
    suspend fun resetPassword(@Body resetPasswordRequest: ResetPasswordRequest): Response<ApiResponse>
}
