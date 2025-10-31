package com.joao.PlantSoS.models

import com.google.gson.annotations.SerializedName

/**
 * Modelo de dados para enviar as informações de um novo utilizador durante o registo.
 * A anotação @SerializedName garante que o nome do campo no JSON seja o correto,
 * mesmo que o nome da variável em Kotlin seja diferente.
 */
data class UserRequest(
    // CORRIGIDO: Agora envia "name" em vez de "nome"
    @SerializedName("name")
    val name: String,

    @SerializedName("email")
    val email: String,

    // CORRIGIDO: Agora envia "password" em vez de "senha"
    @SerializedName("password")
    val password: String
)
