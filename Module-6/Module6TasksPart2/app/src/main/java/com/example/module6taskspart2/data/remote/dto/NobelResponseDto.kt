package com.example.module6taskspart2.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class NobelResponseDto(
    val nobelPrizes: List<NobelPrizeDto> = emptyList()
)