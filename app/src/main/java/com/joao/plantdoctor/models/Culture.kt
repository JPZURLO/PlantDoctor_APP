package com.joao.plantdoctor.models

import com.google.gson.annotations.SerializedName

/**
 * Modelo de dados que representa uma cultura vinda da API.
 */
data class Culture(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("image_url")
    val imageUrl: String,

    // Esta variável é apenas para controlo na UI e não vem da API
    @Transient var isSelected: Boolean = false,
    val imageResId: Int
)
