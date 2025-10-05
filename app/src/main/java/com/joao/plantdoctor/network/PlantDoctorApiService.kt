package com.joao.plantdoctor.network

import com.google.gson.annotations.SerializedName
import com.joao.plantdoctor.models.*
import retrofit2.Response
import retrofit2.http.*

// Data class para enviar uma nova dúvida
data class DoubtRequest(
    @SerializedName("question_text")
    val questionText: String,
    @SerializedName("is_anonymous")
    val isAnonymous: Boolean = false
)

// Data class para enviar um novo plantio
data class PlantedCultureRequest(
    val culture_id: Int,
    val planting_date: String, // Formato "YYYY-MM-DD"
    val notes: String? = null
)

// Data class para enviar um novo evento de histórico
data class HistoryEventRequest(
    val event_type: String, // "ADUBAGEM", "COLHEITA", etc.
    val observation: String
)


data class SuggestionRequest(
    @SerializedName("suggestion_text")
    val suggestionText: String,
    @SerializedName("is_anonymous")
    val isAnonymous: Boolean = false
)

data class CultureRankingItem(
    val name: String,
    val count: Int
)

data class UserUpdateRequest(
    val name: String?,
    val email: String?,
    val password: String?,
    @SerializedName("user_type")
    val userType: String?
)

/**
 * Interface que define todos os endpoints da API PLANT DOCTOR.
 */
interface PlantDoctorApiService {

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
        @Body request: SaveCulturesRequest
    ): Response<ApiResponse>

    // --- Endpoints de Plantio e Histórico ---
    @GET("/api/planted-cultures")
    suspend fun getPlantedCultures(@Header("Authorization") token: String): Response<List<PlantedCulture>>

    @POST("/api/planted-cultures")
    suspend fun addPlantedCulture(
        @Header("Authorization") token: String,
        @Body request: PlantedCultureRequest
    ): Response<PlantedCulture>

    @POST("/api/planted-cultures/{id}/history")
    suspend fun addHistoryEvent(
        @Header("Authorization") token: String,
        @Path("id") plantedCultureId: Int,
        @Body request: HistoryEventRequest
    ): Response<AtividadeHistorico>

    // --- Endpoints de Dúvidas ---
    @GET("api/doubts")
    suspend fun getDoubts(@Header("Authorization") token: String): Response<List<Doubt>>

    @POST("api/doubts")
    suspend fun postDoubt(
        @Header("Authorization") token: String,
        @Body doubtRequest: DoubtRequest
    ): Response<Doubt>

    // --- Endpoints de Sugestões ---
    @GET("api/suggestions")
    suspend fun getSuggestions(@Header("Authorization") token: String): Response<List<Suggestion>>

    @POST("api/suggestions")
    suspend fun postSuggestion(
        @Header("Authorization") token: String,
        @Body suggestionRequest: SuggestionRequest
    ): Response<Suggestion>

    @GET("api/cultures/ranking")
    suspend fun getCultureRanking(@Header("Authorization") token: String): Response<List<CultureRankingItem>>

    @GET("api/admin/users")
    suspend fun getAllUsers(@Header("Authorization") token: String): Response<List<User>>

    @PUT("api/admin/users/{id}")
    suspend fun updateUser(
        @Header("Authorization") token: String,
        @Path("id") userId: Int,
        @Body request: UserUpdateRequest
    ): Response<User>

    @GET("api/admin/users/{id}/history")
    suspend fun getUserHistory(
        @Header("Authorization") token: String,
        @Path("id") userId: Int
    ): Response<List<UserHistoryItem>>
}
