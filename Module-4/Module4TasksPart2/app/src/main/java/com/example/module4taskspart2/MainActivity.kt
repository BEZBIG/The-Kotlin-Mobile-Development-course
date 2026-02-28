package com.example.module4taskspart2

import android.Manifest
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.module4taskspart2.ui.theme.Module4TasksPart2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Module4TasksPart2Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

/**
 * Главный экран: содержит все три задания на одном экране,
 * разделённые горизонтальными линиями.
 */
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    // Лончер запроса разрешения на уведомления
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* разрешение получено или отклонено */ }

    // Проверяем и запрашиваем разрешение при старте
    DisposableEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        onDispose { }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Задание 5: Foreground-сервис
        Task5ForegroundCounter(context = context)

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        // Задание 6: Background-сервис
        Task6BackgroundTimer(context = context)

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        // Задание 7: Bound-сервис
        Task7BoundRandomNumber(context = context)
    }
}

/**
 * Задание 5: UI для foreground-сервиса счётчика.
 * Читает значение из статического поля сервиса каждые 500мс через LaunchedEffect.
 */
@Composable
fun Task5ForegroundCounter(context: Context) {
    var seconds by remember { mutableIntStateOf(0) }
    var isRunning by remember { mutableStateOf(false) }

    // Polling: каждые 500мс читаем значение из статического поля сервиса
    androidx.compose.runtime.LaunchedEffect(isRunning) {
        while (isRunning) {
            seconds = CounterForegroundService.currentSeconds
            kotlinx.coroutines.delay(500)
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Задание 5: Foreground-сервис",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Крупный счётчик секунд
        Text(
            text = "$seconds сек",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val intent = Intent(context, CounterForegroundService::class.java)
                if (isRunning) {
                    context.stopService(intent)
                    seconds = 0
                    isRunning = false
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(intent)
                    } else {
                        context.startService(intent)
                    }
                    isRunning = true
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isRunning) Color(0xFFE53935) else Color(0xFF43A047)
            )
        ) {
            Text(text = if (isRunning) "Стоп" else "Старт")
        }
    }
}

/**
 * Задание 6: UI для background-сервиса таймера.
 */
@Composable
fun Task6BackgroundTimer(context: Context) {
    // Текст, введённый пользователем (количество секунд)
    var inputSeconds by remember { mutableStateOf("10") }
    var isStarted by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Задание 6: Background-сервис",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Поле ввода количества секунд
        OutlinedTextField(
            value = inputSeconds,
            onValueChange = { inputSeconds = it },
            label = { Text("Секунды") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (isStarted) {
            Text(
                text = "Таймер запущен, ждите уведомления...",
                color = Color(0xFF43A047)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                val seconds = inputSeconds.toIntOrNull() ?: 10
                val intent = Intent(context, TimerBackgroundService::class.java).apply {
                    putExtra(EXTRA_TIMER_SECONDS, seconds)
                }
                context.startService(intent)
                isStarted = true
            }
        ) {
            Text("Запустить таймер")
        }
    }
}

/**
 * Задание 7: UI для bound-сервиса случайных чисел.
 */
@Composable
fun Task7BoundRandomNumber(context: Context) {
    var currentNumber by remember { mutableIntStateOf(0) }
    var isBound by remember { mutableStateOf(false) }

    // Ссылка на сервис после привязки
    var randomService by remember { mutableStateOf<RandomNumberService?>(null) }

    // ServiceConnection: обрабатывает подключение и отключение от сервиса
    val serviceConnection = remember {
        object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, binder: IBinder) {
                val service = (binder as RandomNumberService.RandomNumberBinder).getService()
                randomService = service
                // Устанавливаем колбэк для получения новых чисел
                service.onNewNumber = { number ->
                    currentNumber = number
                }
                isBound = true
            }

            override fun onServiceDisconnected(name: ComponentName) {
                randomService = null
                isBound = false
            }
        }
    }

    // Отвязываемся при уходе с экрана (утечка памяти иначе)
    DisposableEffect(Unit) {
        onDispose {
            if (isBound) {
                randomService?.onNewNumber = null
                context.unbindService(serviceConnection)
            }
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Задание 7: Bound-сервис",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Крупное случайное число
        Text(
            text = if (isBound) "$currentNumber" else "—",
            fontSize = 64.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Кнопка Подключиться / Отключиться
        Button(
            onClick = {
                if (isBound) {
                    // Отвязываемся от сервиса
                    randomService?.onNewNumber = null
                    context.unbindService(serviceConnection)
                    randomService = null
                    isBound = false
                } else {
                    // Привязываемся к сервису
                    val intent = Intent(context, RandomNumberService::class.java)
                    context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isBound) Color(0xFFE53935) else Color(0xFF43A047)
            )
        ) {
            Text(text = if (isBound) "Отключиться" else "Подключиться")
        }
    }
}