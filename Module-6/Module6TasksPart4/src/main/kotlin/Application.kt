package com.example.module6taskspart4

import com.example.module6taskspart4.data.database.DatabaseFactory
import com.example.module6taskspart4.plugins.configureAuth
import com.example.module6taskspart4.plugins.configureLogging
import com.example.module6taskspart4.plugins.configureRouting
import com.example.module6taskspart4.plugins.configureSerialization
import com.example.module6taskspart4.plugins.configureStatusPages
import com.example.module6taskspart4.plugins.configureSwagger
import io.ktor.server.application.*
import io.ktor.server.netty.*

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    DatabaseFactory.init()  // ← без параметра

    configureSerialization()
    configureLogging()
    configureAuth()
    configureStatusPages()
    configureSwagger()
    configureRouting()
}