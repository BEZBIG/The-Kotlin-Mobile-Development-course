package com.example.module6taskspart2.domain.usecase

import com.example.module6taskspart2.domain.model.NobelPrize
import com.example.module6taskspart2.domain.repository.NobelRepository

// Получает список премий с фильтрами
class GetNobelPrizesUseCase(private val repository: NobelRepository) {
    suspend operator fun invoke(year: String? = null, category: String? = null): List<NobelPrize> {
        return repository.getPrizes(year, category)
    }
}