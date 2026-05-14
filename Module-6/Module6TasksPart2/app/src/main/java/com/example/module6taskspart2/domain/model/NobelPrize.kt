package com.example.module6taskspart2.domain.model

// Чистая модель премии для UI
data class NobelPrize(
    val year: String,
    val category: String,
    val laureateName: String,
    val motivation: String,
    val country: String
)