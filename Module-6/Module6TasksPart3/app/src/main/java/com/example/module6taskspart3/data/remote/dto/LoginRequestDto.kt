package com.example.module6taskspart3.data.remote.dto

import kotlinx.serialization.Serializable

// Тело запроса на логин
@Serializable
data class LoginRequestDto(
    val username: String,
    val password: String
)