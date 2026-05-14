package com.example.module6taskspart3.domain.usecase

import com.example.module6taskspart3.domain.model.User
import com.example.module6taskspart3.domain.repository.UserRepository

// Получает список пользователей
class GetUsersUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(token: String): List<User> {
        return repository.getUsers(token)
    }
}