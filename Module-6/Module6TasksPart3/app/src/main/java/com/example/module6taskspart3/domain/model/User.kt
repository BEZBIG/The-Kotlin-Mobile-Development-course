package com.example.module6taskspart3.domain.model

// Чистая модель пользователя
data class User(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val username: String,
    val email: String,
    val image: String
)