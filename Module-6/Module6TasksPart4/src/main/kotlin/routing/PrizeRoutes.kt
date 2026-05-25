package com.example.module6taskspart4.routing

import com.example.module6taskspart4.data.repository.PrizeRepositoryImpl
import com.example.module6taskspart4.domain.model.Laureate
import com.example.module6taskspart4.domain.model.NobelPrize
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
    // Временный эндпоинт для заполнения БД тестовыми данными — БЕЗ авторизации
    post("/prizes/seed") {
        val repo = PrizeRepositoryImpl()
        val id = repo.savePrize(
            NobelPrize(
                year = "2023",
                category = "physics",
                laureates = listOf(
                    Laureate("1", "Pierre Agostini", "For attosecond pulses of light", "3"),
                    Laureate("2", "Ferenc Krausz", "For attosecond pulses of light", "3"),
                    Laureate("3", "Anne L'Huillier", "For attosecond pulses of light", "3")
                )
            )
        )
        call.respond(HttpStatusCode.OK, mapOf("prizeId" to id))
    }

    // Защищённые маршруты — требуют JWT токен
    authenticate("auth-jwt") {

        get("/prizes") {
            call.respond(getAllPrizes.execute())
        }

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