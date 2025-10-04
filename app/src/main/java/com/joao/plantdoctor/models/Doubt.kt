package com.joao.plantdoctor.models

import com.google.gson.annotations.SerializedName

data class Doubt(
    @SerializedName("id")
    val id: Int,

    @SerializedName("author_name")
    val author: String,

    @SerializedName("question_text")
    val question: String,

    @SerializedName("created_at")
    val createdAt: String
)