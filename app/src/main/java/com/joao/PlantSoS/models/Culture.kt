package com.joao.PlantSoS.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Modelo de dados que representa uma cultura vinda da API.
 * Implementa Parcelable para ser facilmente passado entre as telas.
 */
@Parcelize
data class Culture(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("image_url")
    val imageUrl: String,

    // Campo para guardar a data de plantio que o usuário escolhe
    // GSON vai procurar por "data_plantio" no JSON. Se não vier da API, pode remover a anotação.
    @SerializedName("data_plantio")
    var dataPlantio: String?,

    // Campo para guardar a data de colheita que vamos calcular
    // GSON vai procurar por "data_colheita_prevista" no JSON.
    @SerializedName("data_colheita_prevista")
    var dataColheitaPrevista: String?,

    // Campo essencial que vem da API
    // GSON vai procurar por "ciclo_em_dias" no JSON.
    @SerializedName("ciclo_em_dias")
    val cicloEmDias: Int,

    // --- Variáveis de controle para a UI (ignoradas pela API/GSON) ---

    @Transient
    var isSelected: Boolean = false,

    @Transient
    val imageResId: Int = 0 // Adicionado valor padrão
) : Parcelable