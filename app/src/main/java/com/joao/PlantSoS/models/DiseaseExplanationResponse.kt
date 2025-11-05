package com.joao.PlantSoS.models

import com.google.gson.annotations.SerializedName

/**
 * Este data class representa as DUAS respostas possíveis da API de explicação:
 * - Ou tem os 3 campos (identificacao, prevencao, tratamento)
 * - Ou tem apenas o campo "mensagem"
 * Por isso, todos são nulos (String?)
 */
data class DiseaseExplanationResponse(

    @SerializedName("identificacao")
    val identificacao: String?,

    @SerializedName("prevencao")
    val prevencao: String?,

    @SerializedName("tratamento")
    val tratamento: String?,

    @SerializedName("mensagem")
    val mensagem: String? // ⬅️ O CAMPO QUE FALTAVA
)