package com.example.module6taskspart4.data.database

import org.jetbrains.exposed.dao.id.IntIdTable

// Таблица нобелевских премий
object PrizeTable : IntIdTable("prizes") {
    val awardYear = varchar("award_year", 10)
    val category = varchar("category", 100)
    val detailLink = varchar("detail_link", 500).nullable()
}