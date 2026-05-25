package com.example.module6taskspart4.plugins

import com.example.module6taskspart4.data.repository.PrizeRepositoryImpl
import com.example.module6taskspart4.data.repository.UserRepositoryImpl
import com.example.module6taskspart4.domain.usecase.*
import com.example.module6taskspart4.routing.authRoutes
import com.example.module6taskspart4.routing.prizeRoutes
import com.example.module6taskspart4.routing.userRoutes
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    val prizeRepository = PrizeRepositoryImpl()
    val userRepository = UserRepositoryImpl()

    val getAllPrizes = GetAllPrizesUseCase(prizeRepository)
    val getPrize = GetPrizeUseCase(prizeRepository)
    val getLaureates = GetLaureatesUseCase(prizeRepository)
    val loginUseCase = LoginUseCase(userRepository)
    val getFavorites = GetFavoritesUseCase(prizeRepository)
    val addFavorite = AddFavoriteUseCase(prizeRepository)
    val removeFavorite = RemoveFavoriteUseCase(prizeRepository)

    routing {
        get("/") { call.respondText("Nobel Prize API with PostgreSQL!") }
        authRoutes(loginUseCase, userRepository)
        prizeRoutes(getAllPrizes, getPrize, getLaureates)
        userRoutes(getFavorites, addFavorite, removeFavorite, userRepository)
    }
}