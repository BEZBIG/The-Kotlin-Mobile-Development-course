package com.example.module6taskspart2.data.remote.dto

import kotlinx.serialization.Serializable

// Один лауреат внутри премии
@Serializable
data class LaureateDto(
    val id: String = "",
    val fullName: FullNameDto? = null,
    val motivation: MotivationDto? = null,
    val birth: BirthDto? = null
)

@Serializable
data class FullNameDto(
    val en: String = ""
)

@Serializable
data class MotivationDto(
    val en: String = ""
)

@Serializable
data class BirthDto(
    val place: PlaceDto? = null
)

@Serializable
data class PlaceDto(
    val country: CountryDto? = null
)

@Serializable
data class CountryDto(
    val en: String = ""
)