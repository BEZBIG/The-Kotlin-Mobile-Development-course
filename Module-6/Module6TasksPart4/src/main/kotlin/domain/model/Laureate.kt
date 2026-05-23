package com.example.module6taskspart4.domain.model

import kotlinx.serialization.Serializable

// Лауреат нобелевской премии
@Serializable
data class Laureate(
    val id: String,
    val name: String,
    val motivation: String,
    val share: String
)