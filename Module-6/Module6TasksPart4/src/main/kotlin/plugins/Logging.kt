package com.example.module6taskspart4.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.calllogging.*
import org.slf4j.event.Level

// Логируем все входящие запросы
fun Application.configureLogging() {
    install(CallLogging) {
        level = Level.INFO
    }
}