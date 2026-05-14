package com.example.module6taskspart3.data.repository

import com.example.module6taskspart3.data.remote.apiGetUserById
import com.example.module6taskspart3.data.remote.apiGetUsers
import com.example.module6taskspart3.data.remote.apiLogin
import com.example.module6taskspart3.domain.model.User
import com.example.module6taskspart3.domain.repository.UserRepository

// Реализация репозитория
class UserRepositoryImpl : UserRepository {

    override suspend fun login(username: String, password: String): String {
        val response = apiLogin(username, password)
        return response.accessToken
    }

    override suspend fun getUsers(token: String): List<User> {
        return apiGetUsers(token).users.map { dto ->
            User(
                id = dto.id,
                firstName = dto.firstName,
                lastName = dto.lastName,
                username = dto.username,
                email = dto.email,
                image = dto.image
            )
        }
    }

    override suspend fun getUserById(id: Int, token: String): User {
        val dto = apiGetUserById(id, token)
        return User(
            id = dto.id,
            firstName = dto.firstName,
            lastName = dto.lastName,
            username = dto.username,
            email = dto.email,
            image = dto.image
        )
    }
}