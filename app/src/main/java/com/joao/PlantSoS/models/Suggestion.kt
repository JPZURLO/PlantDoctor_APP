package com.joao.PlantSoS.models

import com.google.gson.annotations.SerializedName

data class Suggestion(
    @SerializedName("id")
    val id: Int,

    @SerializedName("author_name")
    val author: String,

    @SerializedName("suggestion_text")
    val suggestion: String,

    @SerializedName("created_at")
    val createdAt: String
)