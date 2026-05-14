package com.example.module6taskspart1.domain.model

data class Photo(
    val id: String,
    val author: String,
    val width: Int,
    val height: Int,
    val url: String,
    val downloadUrl: String
) {
    val thumbnailUrl: String
        get() = "https://picsum.photos/id/$id/300/200"
}