package com.joao.plantdoctor.network

import com.joao.plantdoctor.models.ApiResponse
import com.joao.plantdoctor.models.Culture
import com.joao.plantdoctor.models.LoginRequest
import com.joao.plantdoctor.models.LoginResponse
import com.joao.plantdoctor.models.ResetPasswordRequest
import com.joao.plantdoctor.models.SaveCulturesRequest // ✅ 1. ADICIONE ESTE IMPORT
import com.joao.plantdoctor.models.UserRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Interface que define todos os endpoints da API para o Retrofit.
 */
interface ApiService {

    // --- Endpoints de Autenticação ---
    @POST("/api/auth/register")
    suspend fun register(@Body userRequest: UserRequest): Response<ApiResponse>

    @POST("/api/auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @GET("/api/auth/request-password-reset")
    suspend fun requestPasswordReset(@Query("email") email: String): Response<ApiResponse>

    @POST("/api/auth/reset-password")
    suspend fun resetPassword(@Body resetPasswordRequest: ResetPasswordRequest): Response<ApiResponse>


    // --- Endpoints de Culturas ---
    @GET("/api/cultures")
    suspend fun getAllCultures(@Header("Authorization") token: String): Response<List<Culture>>

    @GET("/api/user/my-cultures")
    suspend fun getMyCultures(@Header("Authorization") token: String): Response<List<Culture>>

    @POST("/api/user/cultures")
    suspend fun saveUserCultures(
        @Header("Authorization") token: String,
        // ✅ O TIPO AQUI TEM QUE SER SaveCulturesRequest
        @Body request: SaveCulturesRequest
    ): Response<ApiResponse>
}
