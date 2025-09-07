package com.joao.plantdoctor.models

import com.google.gson.annotations.SerializedName

/**
 * Uma classe de dados genérica para receber respostas simples da API
 * que contêm apenas uma mensagem.
 * (Ex: sucesso no registo, sucesso na redefinição de senha).
 */
data class ApiResponse(
    @SerializedName("message")
    val message: String
)