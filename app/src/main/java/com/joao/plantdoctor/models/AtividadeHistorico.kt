// No arquivo AtividadeHistorico.kt

package com.joao.plantdoctor.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class AtividadeHistorico(
    @SerializedName("id")
    val id: Int,

    @SerializedName("event_date")
    val eventDate: String, // Corresponde ao 'event_date' da API

    @SerializedName("event_type")
    val eventType: String, // Corresponde ao 'event_type' da API

    @SerializedName("observation")
    val observation: String // Corresponde à 'observation' da API

) : Parcelable