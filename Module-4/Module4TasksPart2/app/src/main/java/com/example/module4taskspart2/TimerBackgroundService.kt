// задание 6
package com.example.module4taskspart2

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat

// Канал для уведомления о завершении таймера
private const val TIMER_CHANNEL_ID = "timer_channel"
private const val TIMER_NOTIFICATION_ID = 2001

// Ключ для передачи количества секунд через Intent
const val EXTRA_TIMER_SECONDS = "extra_timer_seconds"

/**
 * Задание 6: Background-сервис.
 * Получает количество секунд, ждёт это время в отдельном потоке,
 * затем показывает уведомление и останавливает сам себя.
 */
class TimerBackgroundService : Service() {

    private var timerThread: Thread? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Получаем количество секунд из Intent (по умолчанию 10)
        val seconds = intent?.getIntExtra(EXTRA_TIMER_SECONDS, 10) ?: 10

        // Запускаем ожидание в отдельном потоке
        timerThread = Thread {
            try {
                Thread.sleep(seconds * 1000L) // Ждём нужное время
                showTimerFinishedNotification() // Показываем уведомление
            } catch (e: InterruptedException) {
                // Поток прерван - ничего не делаем
            } finally {
                stopSelf() // Сервис останавливает сам себя
            }
        }
        timerThread?.start()

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        timerThread?.interrupt()
    }

    /**
     * Создаёт канал уведомлений для таймера.
     */
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            TIMER_CHANNEL_ID,
            "Таймер",
            NotificationManager.IMPORTANCE_HIGH
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    /**
     * Показывает уведомление о завершении таймера.
     */
    private fun showTimerFinishedNotification() {
        val notification = NotificationCompat.Builder(this, TIMER_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Таймер завершён!")
            .setContentText("Время вышло")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(TIMER_NOTIFICATION_ID, notification)
    }
}