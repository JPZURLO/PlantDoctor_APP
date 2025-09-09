package com.joao.plantdoctor.models

import com.google.gson.annotations.SerializedName

/**
 * Classe de dados que representa o corpo (body) da requisição
 * para guardar as culturas selecionadas pelo utilizador.
 * JSON Exemplo: {"culture_ids": [1, 2, 3]}
 */
data class SaveCulturesRequest(
    @SerializedName("culture_ids")
    val cultureIds: List<Int>
)
