package com.example.module6taskspart3.domain.usecase

import com.example.module6taskspart3.domain.repository.UserRepository

// Авторизация — возвращает токен
class LoginUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(username: String, password: String): String {
        return repository.login(username, password)
    }
}