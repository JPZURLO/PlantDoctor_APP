package com.joao.plantdoctor.network

// Imports dos seus modelos de dados existentes
import com.joao.plantdoctor.models.ApiResponse
import com.joao.plantdoctor.models.AtividadeHistorico
import com.joao.plantdoctor.models.Culture
import com.joao.plantdoctor.models.ForecastResponse
import com.joao.plantdoctor.models.LoginRequest
import com.joao.plantdoctor.models.LoginResponse
import com.joao.plantdoctor.models.ResetPasswordRequest
import com.joao.plantdoctor.models.SaveCulturesRequest
import com.joao.plantdoctor.models.UserRequest
import com.joao.plantdoctor.models.WeatherResponse
// Imports do Retrofit
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

// ▼▼▼ NOVOS MODELOS DE DADOS PARA POST (OS QUE ESTAVAM FALTANDO) ▼▼▼
data class PlantedCultureRequest(
    val culture_id: Int,
    val planting_date: String, // Formato "YYYY-MM-DD"
    val notes: String? = null
)

data class HistoryEventRequest(
    val event_type: String, // "ADUBAGEM", "COLHEITA", etc.
    val observation: String
)


/**
 * Interface que define todos os endpoints da API para o Retrofit.
 */
interface ApiService {

    // --- Endpoints de Autenticação (Já existentes) ---
    @POST("/api/auth/register")
    suspend fun register(@Body userRequest: UserRequest): Response<ApiResponse>

    @POST("/api/auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @GET("/api/auth/request-password-reset")
    suspend fun requestPasswordReset(@Query("email") email: String): Response<ApiResponse>

    @POST("/api/auth/reset-password")
    suspend fun resetPassword(@Body resetPasswordRequest: ResetPasswordRequest): Response<ApiResponse>


    // --- Endpoints de Culturas (Já existentes) ---
    @GET("/api/cultures")
    suspend fun getAllCultures(@Header("Authorization") token: String): Response<List<Culture>>

    @GET("/api/user/my-cultures")
    suspend fun getMyCultures(@Header("Authorization") token: String): Response<List<Culture>>

    @POST("/api/user/cultures")
    suspend fun saveUserCultures(
        @Header("Authorization") token: String,
        @Body request: SaveCulturesRequest
    ): Response<ApiResponse>

    // ▼▼▼ NOVOS ENDPOINTS PARA SALVAR DADOS ▼▼▼
    @POST("/api/planted-cultures")
    suspend fun addPlantedCulture(
        @Header("Authorization") token: String,
        @Body request: PlantedCultureRequest
    ): Response<Unit> // Usamos Unit se a resposta for vazia

    @POST("/api/planted-cultures/{id}/history")
    suspend fun addHistoryEvent(
        @Header("Authorization") token: String,
        @Path("id") plantedCultureId: Int,
        @Body request: HistoryEventRequest
    ): Response<AtividadeHistorico>


    // --- Endpoint de Tempo (Já existentes) ---
    @GET("v1/current.json")
    suspend fun getCurrentWeather(
        @Query("key") apiKey: String,
        @Query("q") location: String,
        @Query("aqi") aqi: String = "no",
        @Query("lang") lang: String = "pt"
    ): Response<WeatherResponse>

    @GET("v1/forecast.json")
    suspend fun getForecastWeather(
        @Query("key") apiKey: String,
        @Query("q") location: String,
        @Query("days") days: Int,
        @Query("aqi") aqi: String = "no",
        @Query("alerts") alerts: String = "no",
        @Query("lang") lang: String = "pt"
    ): Response<ForecastResponse>
}