package com.example.module6taskspart4.routing

import com.example.module6taskspart4.domain.usecase.GetAllPrizesUseCase
import com.example.module6taskspart4.domain.usecase.GetLaureatesUseCase
import com.example.module6taskspart4.domain.usecase.GetPrizeUseCase
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.prizeRoutes(
    getAllPrizes: GetAllPrizesUseCase,
    getPrize: GetPrizeUseCase,
    getLaureates: GetLaureatesUseCase
) {
    // Все маршруты ниже защищены JWT
    authenticate("auth-jwt") {

        // Список всех премий
        get("/prizes") {
            call.respond(getAllPrizes.execute())
        }

        // Одна премия по году и категории
        get("/prizes/{year}/{category}") {
            val year = call.parameters["year"] ?: return@get call.respond(
                HttpStatusCode.BadRequest, ErrorResponse("Укажите год")
            )
            val category = call.parameters["category"] ?: return@get call.respond(
                HttpStatusCode.BadRequest, ErrorResponse("Укажите категорию")
            )
            val prize = getPrize.execute(year, category)
            if (prize != null) {
                call.respond(prize)
            } else {
                call.respond(HttpStatusCode.NotFound, ErrorResponse("Премия не найдена"))
            }
        }

        // Список лауреатов премии
        get("/prizes/{year}/{category}/laureates") {
            val year = call.parameters["year"] ?: return@get call.respond(
                HttpStatusCode.BadRequest, ErrorResponse("Укажите год")
            )
            val category = call.parameters["category"] ?: return@get call.respond(
                HttpStatusCode.BadRequest, ErrorResponse("Укажите категорию")
            )
            val laureates = getLaureates.execute(year, category)
            if (laureates.isNotEmpty()) {
                call.respond(laureates)
            } else {
                call.respond(HttpStatusCode.NotFound, ErrorResponse("Лауреаты не найдены"))
            }
        }
    }
}