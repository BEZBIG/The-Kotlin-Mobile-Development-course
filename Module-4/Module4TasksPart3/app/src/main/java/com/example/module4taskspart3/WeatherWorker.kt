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

const val KEY_CITY_NAME = "key_city_name"
const val KEY_TEMPERATURE = "key_temperature"
const val KEY_WEATHER_CONDITION = "key_weather_condition"

private const val WEATHER_CHANNEL_ID = "weather_channel"
private const val WEATHER_NOTIFICATION_ID = 3001

// Возможные погодные условия для имитации
private val CONDITIONS = listOf("ясно", "облачно", "дождь", "снег", "туман")

/**
 * Worker для загрузки погоды одного города.
 * Использует setForeground() — рекомендуемый способ с WorkManager 2.8+.
 */
class WeatherWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val cityName = inputData.getString(KEY_CITY_NAME) ?: "Неизвестный город"

        // Переводим Worker в foreground-режим
        setForeground(createForegroundInfo("Загружаем погоду для $cityName..."))

        return try {
            // Имитируем загрузку погоды
            delay((1500L..3000L).random())

            val temperature = (-10..35).random()
            val condition = CONDITIONS.random()

            Result.success(
                workDataOf(
                    KEY_CITY_NAME to cityName,
                    KEY_TEMPERATURE to temperature,
                    KEY_WEATHER_CONDITION to condition
                )
            )
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun createForegroundInfo(message: String): ForegroundInfo {
        createNotificationChannel()

        val notification = NotificationCompat.Builder(applicationContext, WEATHER_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Прогноз погоды")
            .setContentText(message)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(
                WEATHER_NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            ForegroundInfo(WEATHER_NOTIFICATION_ID, notification)
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            WEATHER_CHANNEL_ID,
            "Прогноз погоды",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = applicationContext
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }
}