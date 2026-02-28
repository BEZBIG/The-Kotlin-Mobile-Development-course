package com.example.module4taskspart3

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.delay

// Ключ для финального результата
const val KEY_UPLOAD_RESULT = "key_upload_result"

/**
 * Задание 8, Worker 3: Имитация загрузки фото в облако.
 * Получает имя файла, "загружает" его и возвращает финальный путь.
 */
class UploadWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val fileName = inputData.getString(KEY_FILE_NAME) ?: "photo.jpg"

            // Имитируем загрузку в облако (2 секунды)
            delay(2000)

            val cloudPath = "https://cloud.example.com/uploads/$fileName"
            val outputData = workDataOf(
                KEY_UPLOAD_RESULT to cloudPath,
                KEY_STEP to "upload"
            )
            Result.success(outputData)
        } catch (e: Exception) {
            Result.failure()
        }
    }
}