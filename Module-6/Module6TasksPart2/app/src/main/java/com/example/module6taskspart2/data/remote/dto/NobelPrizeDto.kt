package com.example.module6taskspart2.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class NobelPrizeDto(
    val year: String = "",
    val category: String = "",
    val laureates: List<LaureateDto> = emptyList()
)