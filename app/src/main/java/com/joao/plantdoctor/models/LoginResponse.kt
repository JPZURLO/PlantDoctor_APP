package com.joao.plantdoctor.models

import com.google.gson.annotations.SerializedName

/**
 * Modelo de dados para receber a resposta do servidor após um login bem-sucedido.
 */
data class LoginResponse(
    @SerializedName("message")
    val message: String,

    @SerializedName("token")
    val token: String? // O token pode ser nulo se o login falhar ou se a API não o enviar.
)
