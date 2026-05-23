package com.example.module6taskspart4.domain.model

import kotlinx.serialization.Serializable

// Нобелевская премия
@Serializable
data class NobelPrize(
    val year: String,
    val category: String,
    val laureates: List<Laureate> = emptyList()
)