package com.example.module6taskspart4.data.database

import org.jetbrains.exposed.dao.id.IntIdTable

// Таблица лауреатов — привязаны к премии
object LaureateTable : IntIdTable("laureates") {
    val prizeId = reference("prize_id", PrizeTable)
    val fullName = varchar("full_name", 255)
    val portion = varchar("portion", 10).default("1")
    val motivation = text("motivation").nullable()
}