package com.example.module6taskspart4.data.database

import org.jetbrains.exposed.dao.id.IntIdTable

// Таблица пользователей
object UserTable : IntIdTable("users") {
    val username = varchar("username", 100).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val role = varchar("role", 50).default("user")
}