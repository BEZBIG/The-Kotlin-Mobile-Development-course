package com.example.module4taskspart5

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Calendar

// Уникальный идентификатор нашего будильника
private const val ALARM_REQUEST_CODE = 1001

/**
 * Устанавливает будильник через 1 минуту от текущего времени (для теста).
 * После проверки уведомления замени на логику с 20:00.
 */
fun scheduleReminder(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    // Создаём Intent для нашего BroadcastReceiver
    val intent = Intent(context, ReminderReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        ALARM_REQUEST_CODE,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // Время срабатывания: текущее время + 1 минута
    val triggerTime = System.currentTimeMillis() + 60_000L

    // На Android 12+ проверяем разрешение на точные будильники
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (alarmManager.canScheduleExactAlarms()) {
            // Разрешение есть - ставим точный будильник
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        } else {
            // Разрешения нет - ставим неточный будильник как запасной вариант
            // Уведомление придёт примерно в нужное время
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }
    } else {
        // Android 11 и ниже - точный будильник без дополнительных проверок
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            pendingIntent
        )
    }
}

/**
 * Отменяет установленный будильник.
 */
fun cancelReminder(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val intent = Intent(context, ReminderReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        ALARM_REQUEST_CODE,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    alarmManager.cancel(pendingIntent)
}

/**
 * Возвращает текст о времени следующего напоминания.
 * Для тестового режима показывает "через 1 минуту".
 */
fun getNextReminderText(): String {
    // После тестирования замени на логику с 20:00
    return "Следующее напоминание: через 1 минуту (тест)"
}