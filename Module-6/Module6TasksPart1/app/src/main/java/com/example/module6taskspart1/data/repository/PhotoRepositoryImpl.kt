package com.example.module6taskspart1.data.repository

import com.example.module6taskspart1.data.remote.PhotoApi
import com.example.module6taskspart1.domain.model.Photo
import com.example.module6taskspart1.domain.repository.PhotoRepository

class PhotoRepositoryImpl(private val api: PhotoApi) : PhotoRepository {

    override suspend fun getPhotos(): List<Photo> {
        return api.getPhotos().map { dto ->
            Photo(
                id = dto.id,
                author = dto.author,
                width = dto.width,
                height = dto.height,
                url = dto.url,
                downloadUrl = dto.downloadUrl
            )
        }
    }
}