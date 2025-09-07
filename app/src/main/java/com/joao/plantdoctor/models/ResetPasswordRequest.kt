package com.joao.plantdoctor.models

import com.google.gson.annotations.SerializedName

/**
 * Modelo de dados para enviar um pedido de redefinição de senha.
 *
 * @param token O token único recebido pelo utilizador (ex: por e-mail).
 * @param newPassword A nova senha que o utilizador deseja definir.
 */
data class ResetPasswordRequest(
    @SerializedName("token")
    val token: String,

    @SerializedName("new_password")
    val newPassword: String
)