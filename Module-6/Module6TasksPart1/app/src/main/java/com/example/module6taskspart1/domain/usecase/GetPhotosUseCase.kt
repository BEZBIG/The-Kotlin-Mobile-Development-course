package com.example.module6taskspart1.domain.usecase

import com.example.module6taskspart1.domain.model.Photo
import com.example.module6taskspart1.domain.repository.PhotoRepository

class GetPhotosUseCase(private val repository: PhotoRepository) {
    suspend operator fun invoke(): List<Photo> {
        return repository.getPhotos()
    }
}