package com.example.module4taskspart2

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat

private const val CHANNEL_ID = "counter_channel"
private const val NOTIFICATION_ID = 1001

const val ACTION_COUNTER_UPDATE = "com.example.module4taskspart2.COUNTER_UPDATE"
const val EXTRA_SECONDS = "extra_seconds"

/**
 * Задание 5: Foreground-сервис счётчика.
 * Хранит текущее значение в статическом поле, чтобы UI мог его читать.
 * Обновляет уведомление каждую секунду.
 */
class CounterForegroundService : Service() {

    private var counterThread: Thread? = null

    @Volatile
    private var isRunning = false

    companion object {
        // Статическое поле: UI читает его напрямую через polling
        @Volatile
        var currentSeconds: Int = 0

        // Флаг: запущен ли сервис прямо сейчас
        @Volatile
        var isServiceRunning: Boolean = false
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        currentSeconds = 0
        isServiceRunning = true

        // Переводим в foreground с начальным уведомлением
        startForeground(NOTIFICATION_ID, buildNotification(0))

        isRunning = true
        counterThread = Thread {
            while (isRunning) {
                // Обновляем статическое поле (UI читает его)
                updateNotification(currentSeconds)

                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    break
                }

                if (isRunning) currentSeconds++
            }
        }
        counterThread?.start()

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        isServiceRunning = false
        currentSeconds = 0
        counterThread?.interrupt()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Счётчик времени",
            NotificationManager.IMPORTANCE_LOW
        )
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    private fun buildNotification(seconds: Int) =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Счётчик времени")
            .setContentText("Прошло $seconds секунд")
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()

    private fun updateNotification(seconds: Int) {
        getSystemService(NotificationManager::class.java)
            .notify(NOTIFICATION_ID, buildNotification(seconds))
    }
}