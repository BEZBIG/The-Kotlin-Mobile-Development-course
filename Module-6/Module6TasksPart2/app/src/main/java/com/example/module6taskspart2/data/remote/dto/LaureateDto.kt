package com.example.module6taskspart2.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LaureateDto(
    val id: String = "",
    val name: String = "",
    val motivation: String = "",
    val share: String = ""
)