package com.example.module6taskspart4.domain.usecase

import com.example.module6taskspart4.domain.model.Laureate
import com.example.module6taskspart4.domain.repository.PrizeRepository

// Возвращает лауреатов конкретной премии
class GetLaureatesUseCase(private val repository: PrizeRepository) {
    fun execute(year: String, category: String): List<Laureate> =
        repository.getLaureates(year, category)
}