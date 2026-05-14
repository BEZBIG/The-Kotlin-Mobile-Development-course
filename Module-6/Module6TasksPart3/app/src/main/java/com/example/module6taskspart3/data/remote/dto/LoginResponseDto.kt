package com.example.module6taskspart3.data.remote.dto

import kotlinx.serialization.Serializable

// Ответ сервера после логина
@Serializable
data class LoginResponseDto(
    val accessToken: String = "",
    val refreshToken: String = "",
    val id: Int = 0,
    val username: String = "",
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val image: String = ""
)