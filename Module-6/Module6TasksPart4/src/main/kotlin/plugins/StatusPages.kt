package com.example.module6taskspart4.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable

@Serializable
data class ApiError(val message: String)

fun Application.configureStatusPages() {
    install(StatusPages) {
        // Возвращаем красивый JSON вместо стека ошибок
        exception<Throwable> { call, cause ->
            call.respond(
                HttpStatusCode.InternalServerError,
                ApiError(cause.message ?: "Внутренняя ошибка сервера")
            )
        }
        status(HttpStatusCode.Unauthorized) { call, _ ->
            call.respond(HttpStatusCode.Unauthorized, ApiError("Требуется авторизация"))
        }
        status(HttpStatusCode.NotFound) { call, _ ->
            call.respond(HttpStatusCode.NotFound, ApiError("Ресурс не найден"))
        }
    }
}