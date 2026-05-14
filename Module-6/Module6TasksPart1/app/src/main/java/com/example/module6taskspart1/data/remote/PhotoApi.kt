package com.example.module6taskspart1.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface PhotoApi {

    // Получить список фотографий с пагинацией
    @GET("v2/list")
    suspend fun getPhotos(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 30
    ): List<PhotoDto>
}