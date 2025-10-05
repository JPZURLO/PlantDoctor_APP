package com.joao.plantdoctor.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize // <-- ADICIONE AQUI
data class User(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("user_type")
    val userType: String
) : Parcelable // <-- E AQUI