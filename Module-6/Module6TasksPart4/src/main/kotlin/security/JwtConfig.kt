package com.example.module6taskspart4.security

// Настройки JWT токена
object JwtConfig {
    const val SECRET = "nobel-prize-api-secret-key-32chars!!"
    const val ISSUER = "com.example.module6taskspart4"
    const val AUDIENCE = "nobel-prize-api-users"
    const val REALM = "Nobel Prize API"
    const val EXPIRATION_MS = 30 * 60 * 1000L // 30 минут
}