package com.example.module4taskspart3

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.delay

/**
 * Задание 8, Worker 2: Имитация добавления водяного знака.
 * Получает имя файла от предыдущего Worker-а и передаёт дальше.
 */
class WatermarkWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            // Читаем имя файла из входных данных (от CompressPhotoWorker)
            val fileName = inputData.getString(KEY_FILE_NAME) ?: "photo.jpg"

            // Имитируем добавление водяного знака (2 секунды)
            delay(2000)

            val outputData = workDataOf(
                KEY_FILE_NAME to fileName.replace("compressed", "watermarked"),
                KEY_STEP to "watermark"
            )
            Result.success(outputData)
        } catch (e: Exception) {
            Result.failure()
        }
    }
}