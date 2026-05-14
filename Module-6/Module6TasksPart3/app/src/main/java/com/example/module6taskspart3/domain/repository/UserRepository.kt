package com.example.module6taskspart3.domain.repository

import com.example.module6taskspart3.domain.model.User

// Интерфейс репозитория
interface UserRepository {
    suspend fun login(username: String, password: String): String
    suspend fun getUsers(token: String): List<User>
    suspend fun getUserById(id: Int, token: String): User
}