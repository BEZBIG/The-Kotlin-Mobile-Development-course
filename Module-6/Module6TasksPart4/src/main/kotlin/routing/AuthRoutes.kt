package com.example.module6taskspart4.routing

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.module6taskspart4.security.JwtConfig
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class LoginRequest(val username: String, val password: String)

@Serializable
data class LoginResponse(val token: String)

@Serializable
data class ErrorResponse(val message: String)

fun Route.authRoutes() {
    post("/auth/login") {
        val request = call.receive<LoginRequest>()

        // Простая проверка логина и пароля
        if (request.username == "admin" && request.password == "password123") {
            val token = JWT.create()
                .withIssuer(JwtConfig.ISSUER)
                .withAudience(JwtConfig.AUDIENCE)
                .withClaim("username", request.username)
                .withExpiresAt(Date(System.currentTimeMillis() + JwtConfig.EXPIRATION_MS))
                .sign(Algorithm.HMAC256(JwtConfig.SECRET))

            call.respond(HttpStatusCode.OK, LoginResponse(token))
        } else {
            call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Неверный логин или пароль"))
        }
    }
}