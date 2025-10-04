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

    // Campo que indica se o usuário já selecionou culturas.
    // Usar Boolean? (nulável) é mais seguro, caso a API não envie este campo.
    @SerializedName("has_cultures")
    val hasCultures: Boolean?,

    // NOVO CAMPO: Indica o tipo de usuário (COMMON ou ADMIN).
    // Também nulável por segurança.
    @SerializedName("user_role")
    val userRole: String?
)