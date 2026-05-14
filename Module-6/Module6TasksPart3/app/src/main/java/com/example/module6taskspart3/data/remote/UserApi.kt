package com.example.module6taskspart3.data.remote

import com.example.module6taskspart3.data.network.KtorClient
import com.example.module6taskspart3.data.remote.dto.LoginRequestDto
import com.example.module6taskspart3.data.remote.dto.LoginResponseDto
import com.example.module6taskspart3.data.remote.dto.UserDto
import com.example.module6taskspart3.data.remote.dto.UsersResponseDto
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

private val client = KtorClient.client
private val baseUrl = KtorClient.BASE_URL

// Отправляем логин и пароль, получаем токен
suspend fun apiLogin(username: String, password: String): LoginResponseDto {
    return client.post("$baseUrl/auth/login") {
        contentType(ContentType.Application.Json)
        setBody(LoginRequestDto(username, password))
    }.body()
}

// Получаем список всех пользователей с токеном
suspend fun apiGetUsers(token: String): UsersResponseDto {
    return client.get("$baseUrl/users") {
        header("Authorization", "Bearer $token")
    }.body()
}

// Получаем одного пользователя по id с токеном
suspend fun apiGetUserById(id: Int, token: String): UserDto {
    return client.get("$baseUrl/users/$id") {
        header("Authorization", "Bearer $token")
    }.body()
}