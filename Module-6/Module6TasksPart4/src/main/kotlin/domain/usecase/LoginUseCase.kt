package com.example.module6taskspart4.domain.usecase

import at.favre.lib.crypto.bcrypt.BCrypt
import com.example.module6taskspart4.domain.model.User
import com.example.module6taskspart4.domain.repository.UserRepository

class LoginUseCase(private val repository: UserRepository) {
    // Проверяем пароль и возвращаем пользователя
    fun execute(username: String, password: String): User? {
        val (user, hash) = repository.findByUsername(username) ?: return null
        val verified = BCrypt.verifyer().verify(password.toCharArray(), hash).verified
        return if (verified) user else null
    }
}