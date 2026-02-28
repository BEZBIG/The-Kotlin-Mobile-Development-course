package com.example.module4taskspart5

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.module4taskspart5.ui.theme.Module4TasksPart5Theme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Module4TasksPart5Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ReminderScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

/**
 * Главный экран приложения.
 * Отображает состояние напоминания и кнопку включения/выключения.
 */
@Composable
fun ReminderScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    // Читаем сохранённое состояние напоминания из SharedPreferences
    val prefs = context.getSharedPreferences("reminder_prefs", Context.MODE_PRIVATE)

    // Состояние: включено ли напоминание
    var isReminderEnabled by remember {
        mutableStateOf(prefs.getBoolean("is_reminder_enabled", false))
    }

    // Лончер для запроса разрешения на уведомления (Android 13+)
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Разрешение получено - включаем напоминание
            enableReminder(context, prefs) { isReminderEnabled = true }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // Заголовок приложения
        Text(
            text = "Напоминание о таблетке",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Индикатор состояния (цветной круг + текст)
        StatusIndicator(isEnabled = isReminderEnabled)

        Spacer(modifier = Modifier.height(24.dp))

        // Текст о времени следующего напоминания (показываем только если включено)
        if (isReminderEnabled) {
            Text(
                text = getNextReminderText(),
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Кнопка включения или выключения напоминания
        Button(
            onClick = {
                if (isReminderEnabled) {
                    // Выключаем напоминание
                    disableReminder(context, prefs) { isReminderEnabled = false }
                } else {
                    // Включаем: сначала проверяем разрешение на уведомления
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val hasPermission = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED

                        if (hasPermission) {
                            enableReminder(context, prefs) { isReminderEnabled = true }
                        } else {
                            // Запрашиваем разрешение
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    } else {
                        // На Android 12 и ниже разрешение не нужно
                        enableReminder(context, prefs) { isReminderEnabled = true }
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isReminderEnabled) Color(0xFFE53935) else Color(0xFF43A047)
            )
        ) {
            Text(
                text = if (isReminderEnabled) "Выключить напоминание" else "Включить напоминание",
                fontSize = 18.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

/**
 * Визуальный индикатор состояния напоминания.
 * Зелёный круг = включено, серый = выключено.
 */
@Composable
fun StatusIndicator(isEnabled: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Цветной круг-индикатор
        val indicatorColor = if (isEnabled) Color(0xFF43A047) else Color(0xFF9E9E9E)
        Spacer(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(indicatorColor)
        )

        // Текст состояния
        Text(
            text = if (isEnabled) "Включено" else "Выключено",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = if (isEnabled) Color(0xFF43A047) else Color(0xFF9E9E9E)
        )
    }
}

/**
 * Включает напоминание: сохраняет состояние и устанавливает будильник.
 */
private fun enableReminder(
    context: Context,
    prefs: android.content.SharedPreferences,
    onDone: () -> Unit
) {
    // Сохраняем состояние в SharedPreferences (нужно для восстановления после перезагрузки)
    prefs.edit().putBoolean("is_reminder_enabled", true).apply()
    // Устанавливаем будильник через AlarmManager
    scheduleReminder(context)
    onDone()
}

/**
 * Выключает напоминание: сохраняет состояние и отменяет будильник.
 */
private fun disableReminder(
    context: Context,
    prefs: android.content.SharedPreferences,
    onDone: () -> Unit
) {
    // Сохраняем состояние в SharedPreferences
    prefs.edit().putBoolean("is_reminder_enabled", false).apply()
    // Отменяем будильник через AlarmManager
    cancelReminder(context)
    onDone()
}