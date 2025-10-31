package com.joao.PlantSoS.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlantedCulture(
    @SerializedName("id")
    val id: Int,

    @SerializedName("planting_date")
    val planting_date: String,

    @SerializedName("predicted_harvest_date")
    val predicted_harvest_date: String?,

    @SerializedName("notes")
    val notes: String?,

    @SerializedName("culture")
    val culture: Culture,

    @SerializedName("history_events")
    val history_events: List<AtividadeHistorico>

) : Parcelable