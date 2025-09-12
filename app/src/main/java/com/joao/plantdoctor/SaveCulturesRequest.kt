package com.joao.plantdoctor.models

import com.google.gson.annotations.SerializedName

/**
 * Modelo de dados para enviar a lista de IDs de culturas para a API.
 * Este objeto será convertido para o JSON: { "culture_ids": [1, 2, 3] }
 */
data class SaveCulturesRequest(

    // A anotação @SerializedName é crucial.
    // Ela diz ao GSON para usar a chave "culture_ids" no JSON,
    // que é o que o seu servidor espera.
    @SerializedName("culture_ids")
    val cultureIds: List<Int>
)