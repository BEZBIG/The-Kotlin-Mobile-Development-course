package com.example.module4taskspart5

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * BroadcastReceiver, который восстанавливает будильник после перезагрузки устройства.
 * Android отменяет все будильники при выключении телефона,
 * поэтому нужно переустановить их при старте системы.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        // Проверяем, что это именно событие завершения загрузки
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            // Читаем из SharedPreferences, было ли напоминание включено
            val prefs = context.getSharedPreferences("reminder_prefs", Context.MODE_PRIVATE)
            val isEnabled = prefs.getBoolean("is_reminder_enabled", false)

            // Если напоминание было включено - восстанавливаем будильник
            if (isEnabled) {
                scheduleReminder(context)
            }
        }
    }
}