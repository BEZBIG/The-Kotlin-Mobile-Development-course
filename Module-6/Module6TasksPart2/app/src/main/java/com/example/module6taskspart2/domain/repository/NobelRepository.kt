package com.example.module6taskspart2.domain.repository

import com.example.module6taskspart2.domain.model.NobelPrize

interface NobelRepository {
    suspend fun getPrizes(year: String? = null, category: String? = null): List<NobelPrize>
}