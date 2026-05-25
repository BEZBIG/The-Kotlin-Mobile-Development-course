package com.example.module6taskspart4.plugins

import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.ktor.server.application.*

fun Application.configureSwagger() {
    install(SwaggerUI) {
        swagger {
            swaggerUrl = "swagger"
            forwardRoot = false
        }
        info {
            title = "Nobel Prize API"
            version = "1.0.0"
            description = "API для работы с нобелевскими премиями"
        }
    }
}