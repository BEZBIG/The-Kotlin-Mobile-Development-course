package com.example.module4taskspart5

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

// Идентификаторы для канала уведомлений
private const val CHANNEL_ID = "reminder_channel"
private const val NOTIFICATION_ID = 2001

/**
 * BroadcastReceiver, который срабатывает в 20:00.
 * Показывает уведомление и планирует следующий будильник на завтра.
 */
class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        // Показываем уведомление пользователю
        showNotification(context)

        // Планируем следующий будильник на 20:00 следующего дня
        scheduleReminder(context)
    }

    /**
     * Создаёт канал уведомлений (нужно для Android 8+) и показывает уведомление.
     */
    private fun showNotification(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Создаём канал уведомлений для Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Напоминание о таблетке",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Ежедневное напоминание о приёме таблетки"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Строим само уведомление
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Напоминание о таблетке")
            .setContentText("Время принять таблетку!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true) // Уведомление исчезает после нажатия
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}