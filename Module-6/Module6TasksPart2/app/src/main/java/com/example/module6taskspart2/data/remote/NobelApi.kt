package com.example.module6taskspart2.data.remote

import com.example.module6taskspart2.data.remote.dto.NobelPrizeDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private val client = HttpClient(Android) {
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
}

// Получаем список премий с нашего сервера с токеном
suspend fun fetchNobelPrizes(token: String): List<NobelPrizeDto> {
    return client.get("http://10.0.2.2:8080/prizes") {
        header("Authorization", "Bearer $token")
    }.body()
}