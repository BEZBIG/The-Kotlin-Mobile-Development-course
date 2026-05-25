package com.example.module6taskspart4.domain.repository

import com.example.module6taskspart4.domain.model.Laureate
import com.example.module6taskspart4.domain.model.NobelPrize

interface PrizeRepository {
    fun getAllPrizes(): List<NobelPrize>
    fun getPrize(year: String, category: String): NobelPrize?
    fun getLaureates(year: String, category: String): List<Laureate>
    fun savePrize(prize: NobelPrize): Int
    fun getFavorites(userId: Int): List<NobelPrize>
    fun addFavorite(userId: Int, prizeId: Int)
    fun removeFavorite(userId: Int, prizeId: Int)
}