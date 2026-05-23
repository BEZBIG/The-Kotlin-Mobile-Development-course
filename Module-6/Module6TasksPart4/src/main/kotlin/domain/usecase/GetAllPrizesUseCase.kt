package com.example.module6taskspart4.domain.usecase

import com.example.module6taskspart4.domain.model.NobelPrize
import com.example.module6taskspart4.domain.repository.PrizeRepository

// Возвращает все премии
class GetAllPrizesUseCase(private val repository: PrizeRepository) {
    fun execute(): List<NobelPrize> = repository.getAllPrizes()
}