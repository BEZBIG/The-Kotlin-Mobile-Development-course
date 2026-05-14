package com.example.module6taskspart3.data.remote.dto

import kotlinx.serialization.Serializable

// Ответ от /users — список пользователей
@Serializable
data class UsersResponseDto(
    val users: List<UserDto> = emptyList()
)