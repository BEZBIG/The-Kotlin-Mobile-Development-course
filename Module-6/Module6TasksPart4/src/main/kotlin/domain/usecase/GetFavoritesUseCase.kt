package com.example.module6taskspart4.domain.usecase

import com.example.module6taskspart4.domain.model.NobelPrize
import com.example.module6taskspart4.domain.repository.PrizeRepository

class GetFavoritesUseCase(private val repository: PrizeRepository) {
    fun execute(userId: Int): List<NobelPrize> = repository.getFavorites(userId)
}