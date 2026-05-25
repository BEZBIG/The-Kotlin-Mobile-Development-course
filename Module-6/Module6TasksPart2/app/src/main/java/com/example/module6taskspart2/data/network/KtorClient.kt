package com.example.module6taskspart2.data.network

import com.example.module6taskspart2.data.remote.dto.NobelResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object KtorClient {
    private const val BASE_URL = "http://10.0.2.2:8080/"

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
        install(Logging) {
            level = LogLevel.INFO
        }
    }

    // GET запрос с токеном авторизации
    suspend fun get(
        path: String,
        params: Map<String, String> = emptyMap(),
        token: String? = null
    ): NobelResponseDto {
        return client.get("$BASE_URL$path") {
            params.forEach { (key, value) -> parameter(key, value) }
            if (token != null) header("Authorization", "Bearer $token")
        }.body()
    }
}