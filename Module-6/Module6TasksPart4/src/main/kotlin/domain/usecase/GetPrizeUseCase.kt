package com.example.module6taskspart4.domain.usecase

import com.example.module6taskspart4.domain.model.NobelPrize
import com.example.module6taskspart4.domain.repository.PrizeRepository

// Возвращает одну премию по году и категории
class GetPrizeUseCase(private val repository: PrizeRepository) {
    fun execute(year: String, category: String): NobelPrize? =
        repository.getPrize(year, category)
}