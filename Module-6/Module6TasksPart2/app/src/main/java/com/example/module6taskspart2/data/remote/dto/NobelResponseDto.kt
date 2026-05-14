package com.example.module6taskspart2.data.remote.dto

import kotlinx.serialization.Serializable

// Корневой объект ответа от API
@Serializable
data class NobelResponseDto(
    val nobelPrizes: List<NobelPrizeDto> = emptyList()
)