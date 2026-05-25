package com.example.module6taskspart4.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val username: String,
    val role: String
)