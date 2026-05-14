package com.example.module6taskspart2.data.remote.dto

import kotlinx.serialization.Serializable

// Одна нобелевская премия с лауреатами
@Serializable
data class NobelPrizeDto(
    val awardYear: String = "",
    val category: CategoryDto? = null,
    val laureates: List<LaureateDto> = emptyList()
)

@Serializable
data class CategoryDto(
    val en: String = ""
)