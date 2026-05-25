package com.example.module6taskspart4.routing

import com.example.module6taskspart4.domain.repository.UserRepository
import com.example.module6taskspart4.domain.usecase.AddFavoriteUseCase
import com.example.module6taskspart4.domain.usecase.GetFavoritesUseCase
import com.example.module6taskspart4.domain.usecase.RemoveFavoriteUseCase
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

// Отдельный класс для ответа профиля
@Serializable
data class UserProfileResponse(
    val username: String,
    val userId: Int
)

@Serializable
data class StatusResponse(val status: String)

fun Route.userRoutes(
    getFavorites: GetFavoritesUseCase,
    addFavorite: AddFavoriteUseCase,
    removeFavorite: RemoveFavoriteUseCase,
    userRepository: UserRepository
) {
    authenticate("auth-jwt") {

        // Профиль текущего пользователя
        get("/users/me") {
            val principal = call.principal<JWTPrincipal>()
            val username = principal?.payload?.getClaim("username")?.asString() ?: ""
            val userId = principal?.payload?.getClaim("userId")?.asInt() ?: 0
            call.respond(UserProfileResponse(username = username, userId = userId))
        }

        // Список избранных премий
        get("/users/me/prizes") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.payload?.getClaim("userId")?.asInt() ?: return@get
            call.respond(getFavorites.execute(userId))
        }

        // Добавить премию в избранное
        post("/users/me/prizes/{prizeId}") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.payload?.getClaim("userId")?.asInt() ?: return@post
            val prizeId = call.parameters["prizeId"]?.toIntOrNull()
                ?: return@post call.respond(HttpStatusCode.BadRequest, ErrorResponse("Неверный id"))
            addFavorite.execute(userId, prizeId)
            call.respond(HttpStatusCode.OK, StatusResponse("добавлено"))
        }

        // Удалить премию из избранного
        delete("/users/me/prizes/{prizeId}") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.payload?.getClaim("userId")?.asInt() ?: return@delete
            val prizeId = call.parameters["prizeId"]?.toIntOrNull()
                ?: return@delete call.respond(HttpStatusCode.BadRequest, ErrorResponse("Неверный id"))
            removeFavorite.execute(userId, prizeId)
            call.respond(HttpStatusCode.OK, StatusResponse("удалено"))
        }
    }
}