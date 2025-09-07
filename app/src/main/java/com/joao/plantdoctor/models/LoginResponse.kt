package com.joao.plantdoctor.models

import com.google.gson.annotations.SerializedName

/**
 * Modelo de dados para receber a resposta do servidor após um login bem-sucedido.
 */
data class LoginResponse(
    @SerializedName("message")
    val message: String,

    @SerializedName("token")
    val token: String?,

    // ✅ NOVO CAMPO: Indica se o utilizador já selecionou culturas.
    @SerializedName("has_cultures")
    val hasCultures: Boolean
)
