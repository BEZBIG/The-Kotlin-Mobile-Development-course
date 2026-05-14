package com.example.module6taskspart3.data.remote.dto

import kotlinx.serialization.Serializable

// Один пользователь из списка
@Serializable
data class UserDto(
    val id: Int = 0,
    val firstName: String = "",
    val lastName: String = "",
    val username: String = "",
    val email: String = "",
    val image: String = ""
)