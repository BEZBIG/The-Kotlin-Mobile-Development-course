package com.example.module6taskspart4.domain.repository

import com.example.module6taskspart4.domain.model.User

interface UserRepository {
    fun findByUsername(username: String): Pair<User, String>? // User + passwordHash
    fun createUser(username: String, passwordHash: String, role: String): User
}