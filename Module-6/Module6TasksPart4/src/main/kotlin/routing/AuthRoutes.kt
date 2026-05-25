package com.example.module6taskspart4.routing

import at.favre.lib.crypto.bcrypt.BCrypt
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.module6taskspart4.domain.repository.UserRepository
import com.example.module6taskspart4.domain.usecase.LoginUseCase
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

fun Route.authRoutes(loginUseCase: LoginUseCase, userRepository: UserRepository) {

    // Логин — возвращает JWT токен
    post("/login") {
        val request = call.receive<LoginRequest>()
        val user = loginUseCase.execute(request.username, request.password)

        if (user != null) {
            val token = JWT.create()
                .withIssuer(JwtConfig.ISSUER)
                .withAudience(JwtConfig.AUDIENCE)
                .withClaim("username", user.username)
                .withClaim("userId", user.id)
                .withExpiresAt(Date(System.currentTimeMillis() + JwtConfig.EXPIRATION_MS))
                .sign(Algorithm.HMAC256(JwtConfig.SECRET))
            call.respond(HttpStatusCode.OK, LoginResponse(token))
        } else {
            call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Неверный логин или пароль"))
        }
    }

    // Регистрация нового пользователя
    post("/register") {
        val request = call.receive<LoginRequest>()
        val hash = BCrypt.withDefaults().hashToString(12, request.password.toCharArray())
        val user = userRepository.createUser(request.username, hash, "user")
        call.respond(HttpStatusCode.Created, mapOf("id" to user.id, "username" to user.username))
    }
}