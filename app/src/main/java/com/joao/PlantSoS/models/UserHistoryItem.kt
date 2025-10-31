package com.joao.PlantSoS.models

import com.google.gson.annotations.SerializedName

data class UserHistoryItem(
    @SerializedName("editor_name")
    val editorName: String,

    @SerializedName("field_changed")
    val fieldChanged: String,

    @SerializedName("old_value")
    val oldValue: String,

    @SerializedName("new_value")
    val newValue: String,

    @SerializedName("changed_at")
    val changedAt: String // A API envia como string no formato ISO
)