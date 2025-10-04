package com.joao.plantdoctor.models

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("user_type")
    val userType: String // "COMMON" ou "ADMIN"
)