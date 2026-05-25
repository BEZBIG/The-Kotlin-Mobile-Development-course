package com.example.module6taskspart4.domain.usecase

import com.example.module6taskspart4.domain.repository.PrizeRepository

class AddFavoriteUseCase(private val repository: PrizeRepository) {
    fun execute(userId: Int, prizeId: Int) = repository.addFavorite(userId, prizeId)
}