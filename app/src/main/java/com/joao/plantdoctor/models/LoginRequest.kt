package com.joao.plantdoctor.models

// O import correto para a anotação
import com.google.gson.annotations.SerializedName

/**
 * Modelo de dados para enviar na requisição de login.
 */
data class LoginRequest(
    // A anotação @SerializedName mapeia o nome da variável no Kotlin ("email")
    // para o nome da chave no JSON que o servidor espera ("email").
    @SerializedName("email")
    val email: String,

    // CORRIGIDO: A anotação estava com um erro de digitação.
    // Mapeia a variável "senha" para a chave JSON "password".
    @SerializedName("password")
    val password: String
)

