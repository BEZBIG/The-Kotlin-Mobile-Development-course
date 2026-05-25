package com.example.module6taskspart4.data.database

import org.jetbrains.exposed.sql.Table

// Таблица избранных премий пользователя
object UserPrizeTable : Table("user_prizes") {
    val userId = reference("user_id", UserTable)
    val prizeId = reference("prize_id", PrizeTable)
    override val primaryKey = PrimaryKey(userId, prizeId)
}