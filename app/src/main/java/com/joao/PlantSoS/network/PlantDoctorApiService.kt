package com.joao.PlantSoS.network

import com.joao.PlantSoS.models.* // Importa TODOS os seus modelos
import com.joao.PlantSoS.models.SaveCulturesRequest
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response // Importante para suspend funs
import retrofit2.http.*

interface PlantDoctorApiService {

    // --- IA (Corrigido: removida a barra "/" inicial) ---
    @Multipart
    @POST("predict") // Rota: /api/predict
    fun predictDisease(
        @Part image: MultipartBody.Part
    ): Call<DiseaseExplanationResponse>

    @GET("explanations/{disease_name}")
    suspend fun getDiseaseExplanation(
        @Path("disease_name") diseaseName: String
    ): Response<DiseaseExplanationResponse>

    @GET("invalid_image") // Rota: /api/invalid_image
    fun getInvalidImageMessage(): Call<Map<String, String>>


    // --- Autenticação (Já estava correta) ---
    @POST("auth/register")
    suspend fun register(
        @Body request: UserRequest
    ): Response<ApiResponse>

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("auth/request-reset")
    suspend fun requestPasswordReset(
        @Query("email") email: String
    ): Response<ApiResponse>

    @POST("auth/reset-password")
    suspend fun resetPassword(
        @Body request: ResetPasswordRequest
    ): Response<ApiResponse>


    // --- Culturas (Corrigido para bater com o Flask) ---
    @GET("cultures") // Rota no Flask: /api/cultures
    suspend fun getAllCultures(
        @Header("Authorization") token: String
    ): Response<List<Culture>>

    @GET("user/my-cultures") // Rota no Flask: /api/user/my-cultures
    suspend fun getMyCultures(
        @Header("Authorization") token: String
    ): Response<List<Culture>>

    @POST("user/cultures") // Rota no Flask: /api/user/cultures
    suspend fun saveUserCultures(
        @Header("Authorization") token: String,
        @Body request: SaveCulturesRequest
    ): Response<ApiResponse>


    // --- Plantios e Histórico de Atividades (Já estava correto) ---
    @GET("planted-cultures") // Rota no Flask: /api/planted-cultures
    suspend fun getPlantedCultures(
        @Header("Authorization") token: String
    ): Response<List<PlantedCulture>>

    @POST("planted-cultures") // Rota no Flask: /api/planted-cultures
    suspend fun addPlantedCulture(
        @Header("Authorization") token: String,
        @Body request: PlantedCultureRequest
    ): Response<PlantedCulture>

    @POST("planted-cultures/{id}/history") // Rota no Flask: /api/planted-cultures/{id}/history
    suspend fun addHistoryEvent(
        @Header("Authorization") token: String,
        @Path("id") plantedCultureId: Int,
        @Body request: HistoryEventRequest
    ): Response<AtividadeHistorico>


    // --- Histórico de Diagnóstico (Corrigido para bater com o Flask) ---

    // Rota no Flask: /api/cultures/<int:culture_id>/diagnosis-history
    @GET("cultures/{culture_id}/diagnosis-history")
    suspend fun getDiagnosisHistory(
        @Header("Authorization") token: String,
        @Path("culture_id") cultureId: Int // Adicionado o ID da cultura
    ): Response<List<DiagnosisHistoryItem>>

    // Rota no Flask: /api/diagnosis-history
    @POST("diagnosis-history")
    suspend fun saveDiagnosis(
        @Header("Authorization") token: String,
        @Body request: DiagnosisRequest
    ): Response<ApiResponse>

    // Rota no Flask: /api/admin/users/<int:user_id>/history
    @GET("admin/users/{id}/history")
    suspend fun getUserHistory(
        @Header("Authorization") token: String, // Adicionado o token
        @Path("id") userId: Int
    ): Response<List<DiagnosisHistoryItem>>


    // --- Dúvidas (Já estava correto) ---
    @GET("doubts")
    suspend fun getDoubts(
        @Header("Authorization") token: String
    ): Response<List<Doubt>>

    @POST("doubts")
    suspend fun postDoubt(
        @Header("Authorization") token: String,
        @Body request: DoubtRequest
    ): Response<Doubt>


    // --- Sugestões (Já estava correto) ---
    @GET("suggestions")
    suspend fun getSuggestions(
        @Header("Authorization") token: String
    ): Response<List<Suggestion>>

    @POST("suggestions")
    suspend fun postSuggestion(
        @Header("Authorization") token: String,
        @Body request: SuggestionRequest
    ): Response<Suggestion>


    // --- Admin: Usuários (Já estava correto) ---
    @GET("admin/users")
    suspend fun getAllUsers(
        @Header("Authorization") token: String
    ): Response<List<User>>

    @PUT("admin/users/{id}")
    suspend fun updateUser(
        @Header("Authorization") token: String,
        @Path("id") userId: Int,
        @Body request: UserUpdateRequest
    ): Response<User>


    // --- Ranking (Já estava correto) ---
    @GET("cultures/ranking")
    suspend fun getCultureRanking(
        @Header("Authorization") token: String
    ): Response<List<CultureRankingItem>>



}