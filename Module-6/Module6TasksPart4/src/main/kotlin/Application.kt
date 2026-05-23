package com.example.module6taskspart4

import com.example.module6taskspart4.plugins.configureAuth
import com.example.module6taskspart4.plugins.configureLogging
import com.example.module6taskspart4.plugins.configureRouting
import com.example.module6taskspart4.plugins.configureSerialization
import io.ktor.server.application.*
import io.ktor.server.netty.*

// Точка входа — запускаем сервер на порту 8080
fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    configureSerialization()
    configureLogging()
    configureAuth()
    configureRouting()
}