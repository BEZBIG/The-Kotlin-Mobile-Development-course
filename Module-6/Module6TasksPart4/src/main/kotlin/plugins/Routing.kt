package com.example.module6taskspart4.plugins

import com.example.module6taskspart4.data.repository.PrizeRepositoryImpl
import com.example.module6taskspart4.domain.usecase.GetAllPrizesUseCase
import com.example.module6taskspart4.domain.usecase.GetLaureatesUseCase
import com.example.module6taskspart4.domain.usecase.GetPrizeUseCase
import com.example.module6taskspart4.routing.authRoutes
import com.example.module6taskspart4.routing.prizeRoutes
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

// Регистрируем все маршруты приложения
fun Application.configureRouting() {
    val repository = PrizeRepositoryImpl()
    val getAllPrizes = GetAllPrizesUseCase(repository)
    val getPrize = GetPrizeUseCase(repository)
    val getLaureates = GetLaureatesUseCase(repository)

    routing {
        // Health check
        get("/") {
            call.respondText("Nobel Prize API is running!")
        }
        authRoutes()
        prizeRoutes(getAllPrizes, getPrize, getLaureates)
    }
}