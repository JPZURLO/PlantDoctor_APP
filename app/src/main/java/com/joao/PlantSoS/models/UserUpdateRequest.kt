package com.joao.PlantSoS.models
// (ou com.joao.PlantSoS.models)

data class UserUpdateRequest(
    val name: String,
    val email: String,
    val password: String?,
    val role: String
)