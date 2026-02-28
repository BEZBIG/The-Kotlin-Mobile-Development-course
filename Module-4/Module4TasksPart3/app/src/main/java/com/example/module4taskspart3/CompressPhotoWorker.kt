package com.example.module4taskspart3

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.delay

// Ключи для передачи данных между Worker-ами
const val KEY_FILE_NAME = "key_file_name"
const val KEY_STEP = "key_step"

/**
 * Задание 8, Worker 1: Имитация сжатия фото.
 * Ждёт 2 секунды, затем передаёт имя файла следующему Worker-у.
 */
class CompressPhotoWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            // Имитируем долгую операцию сжатия (2 секунды)
            delay(2000)

            // Передаём имя файла дальше по цепочке
            val outputData = workDataOf(
                KEY_FILE_NAME to "photo_compressed.jpg",
                KEY_STEP to "compress"
            )
            Result.success(outputData)
        } catch (e: Exception) {
            Result.failure()
        }
    }
}