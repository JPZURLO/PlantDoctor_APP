// network/RetrofitClient.kt

package com.joao.plantdoctor.network // Confirme se o seu package está correto

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "https://plantdoctor-backend.onrender.com/" // SUBSTITUA PELA SUA URL REAL

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // ✅ PONTO CRÍTICO: Verifique esta parte com atenção!
    // A propriedade deve ser pública (não ter 'private' na frente)
    // e o nome deve ser exatamente 'apiService'.
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
