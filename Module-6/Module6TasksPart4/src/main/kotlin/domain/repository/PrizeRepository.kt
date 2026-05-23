package com.example.module6taskspart4.domain.repository

import com.example.module6taskspart4.domain.model.Laureate
import com.example.module6taskspart4.domain.model.NobelPrize

// Интерфейс для работы с данными о премиях
interface PrizeRepository {
    fun getAllPrizes(): List<NobelPrize>
    fun getPrize(year: String, category: String): NobelPrize?
    fun getLaureates(year: String, category: String): List<Laureate>
}