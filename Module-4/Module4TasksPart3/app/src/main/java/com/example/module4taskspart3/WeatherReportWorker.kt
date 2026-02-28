package com.example.module4taskspart3

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.delay

const val KEY_REPORT_RESULT = "key_report_result"
const val KEY_CITIES_JSON = "key_cities_json"

private const val REPORT_CHANNEL_ID = "report_channel"
private const val REPORT_NOTIFICATION_ID = 3002

/**
 * Финальный Worker: формирует итоговый отчёт по погоде.
 * Получает данные о городах через inputData и обновляет уведомление.
 */
class WeatherReportWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        // Показываем уведомление "формируем отчёт"
        setForeground(createForegroundInfo("Все данные получены, формируем отчёт..."))

        return try {
            delay(1500)

            // Читаем данные городов из JSON-строки
            val citiesJson = inputData.getString(KEY_CITIES_JSON) ?: "[]"
            val avgTemp = inputData.getInt(KEY_TEMPERATURE, 0)

            val report = "Отчёт готов! Средняя температура: +${avgTemp}°C"

            // Обновляем уведомление с финальным результатом
            showFinalNotification(report)

            Result.success(
                workDataOf(
                    KEY_REPORT_RESULT to report,
                    KEY_CITIES_JSON to citiesJson,
                    KEY_TEMPERATURE to avgTemp
                )
            )
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun createForegroundInfo(message: String): ForegroundInfo {
        createNotificationChannel()

        val notification = NotificationCompat.Builder(applicationContext, REPORT_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Прогноз погоды")
            .setContentText(message)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(
                REPORT_NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            ForegroundInfo(REPORT_NOTIFICATION_ID, notification)
        }
    }

    private fun showFinalNotification(report: String) {
        createNotificationChannel()
        val manager = applicationContext
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(applicationContext, REPORT_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Прогноз погоды")
            .setContentText(report)
            .setOngoing(false)
            .setAutoCancel(true)
            .build()

        // Убираем ongoing-уведомление и показываем финальное
        manager.cancel(REPORT_NOTIFICATION_ID)
        manager.notify(REPORT_NOTIFICATION_ID + 1, notification)
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            REPORT_CHANNEL_ID,
            "Отчёт о погоде",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = applicationContext
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }
}