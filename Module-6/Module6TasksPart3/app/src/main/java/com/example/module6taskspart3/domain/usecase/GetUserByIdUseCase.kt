package com.example.module6taskspart3.domain.usecase

import com.example.module6taskspart3.domain.model.User
import com.example.module6taskspart3.domain.repository.UserRepository

// Получает одного пользователя по id
class GetUserByIdUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(id: Int, token: String): User {
        return repository.getUserById(id, token)
    }
}