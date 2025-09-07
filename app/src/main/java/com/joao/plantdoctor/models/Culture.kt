package com.joao.plantdoctor.models

data class Culture(
    val name: String,
    val imageResId: Int,
    var isSelected: Boolean = false
)