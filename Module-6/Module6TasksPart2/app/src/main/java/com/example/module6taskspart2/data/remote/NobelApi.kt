package com.example.module6taskspart2.data.remote

import com.example.module6taskspart2.data.remote.dto.NobelResponseDto
import com.example.module6taskspart2.data.network.KtorClient

// Делает запрос к API и возвращает список премий
suspend fun fetchNobelPrizes(year: String? = null, category: String? = null): NobelResponseDto {
    val params = buildMap {
        put("limit", "25")
        put("offset", "0")
        if (year != null) put("nobelPrizeYear", year)
        if (category != null) put("nobelPrizeCategory", category)
    }
    return KtorClient.get("nobelPrizes", params)
}