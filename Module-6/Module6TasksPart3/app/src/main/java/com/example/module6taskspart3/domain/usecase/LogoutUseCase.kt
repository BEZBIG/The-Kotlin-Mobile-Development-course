package com.example.module6taskspart3.domain.usecase

import com.example.module6taskspart3.data.local.TokenStorage

// Удаляет токен из хранилища
class LogoutUseCase(private val tokenStorage: TokenStorage) {
    suspend operator fun invoke() {
        tokenStorage.clearToken()
    }
}