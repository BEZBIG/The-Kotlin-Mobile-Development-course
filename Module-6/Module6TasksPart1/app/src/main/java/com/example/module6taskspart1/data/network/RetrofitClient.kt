package com.example.module6taskspart1.data.network

import com.example.module6taskspart1.data.remote.PhotoApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Создаём и настраиваем Retrofit один раз
object RetrofitClient {

    private const val BASE_URL = "https://picsum.photos/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val photoApi: PhotoApi = retrofit.create(PhotoApi::class.java)
}