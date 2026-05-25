package com.example.module6taskspart2.data.repository

import com.example.module6taskspart2.data.local.TokenStorage
import com.example.module6taskspart2.data.remote.fetchNobelPrizes
import com.example.module6taskspart2.domain.model.NobelPrize
import com.example.module6taskspart2.domain.repository.NobelRepository
import kotlinx.coroutines.flow.first

class NobelRepositoryImpl(private val tokenStorage: TokenStorage) : NobelRepository {

    override suspend fun getPrizes(year: String?, category: String?): List<NobelPrize> {
        val token = tokenStorage.tokenFlow.first() ?: ""
        val dtos = fetchNobelPrizes(token)

        return dtos
            .filter { dto ->
                (year == null || dto.year == year) &&
                        (category == null || dto.category.lowercase() == category.lowercase())
            }
            .flatMap { dto ->
                if (dto.laureates.isEmpty()) {
                    listOf(
                        NobelPrize(
                            year = dto.year,
                            category = dto.category,
                            laureateName = "Не присуждалась",
                            motivation = "",
                            country = ""
                        )
                    )
                } else {
                    dto.laureates.map { laureate ->
                        NobelPrize(
                            year = dto.year,
                            category = dto.category,
                            laureateName = laureate.name,
                            motivation = laureate.motivation,
                            country = ""
                        )
                    }
                }
            }
    }
}