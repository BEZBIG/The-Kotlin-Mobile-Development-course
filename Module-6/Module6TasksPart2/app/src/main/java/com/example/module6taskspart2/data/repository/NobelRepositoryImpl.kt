package com.example.module6taskspart2.data.repository

import com.example.module6taskspart2.data.remote.fetchNobelPrizes
import com.example.module6taskspart2.domain.model.NobelPrize
import com.example.module6taskspart2.domain.repository.NobelRepository

class NobelRepositoryImpl : NobelRepository {

    override suspend fun getPrizes(year: String?, category: String?): List<NobelPrize> {
        val response = fetchNobelPrizes(year, category)
        val result = mutableListOf<NobelPrize>()

        for (prize in response.nobelPrizes) {
            val laureates = prize.laureates

            if (laureates.isEmpty()) {
                result.add(
                    NobelPrize(
                        year = prize.awardYear,
                        category = prize.category?.en ?: "",
                        laureateName = "Не присуждалась",
                        motivation = "",
                        country = ""
                    )
                )
            } else {
                for (laureate in laureates) {
                    result.add(
                        NobelPrize(
                            year = prize.awardYear,
                            category = prize.category?.en ?: "",
                            laureateName = laureate.fullName?.en ?: "Организация",
                            motivation = laureate.motivation?.en ?: "",
                            country = laureate.birth?.place?.country?.en ?: ""
                        )
                    )
                }
            }
        }

        return result
    }
}