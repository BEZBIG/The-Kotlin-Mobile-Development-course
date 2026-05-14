package com.example.module6taskspart1.domain.repository

import com.example.module6taskspart1.domain.model.Photo

interface PhotoRepository {
    suspend fun getPhotos(): List<Photo>
}