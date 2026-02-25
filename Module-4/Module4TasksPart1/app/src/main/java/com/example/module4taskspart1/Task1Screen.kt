package com.example.module4taskspart1

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import kotlin.system.measureTimeMillis

@Composable
fun Task1Screen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val json = Json { ignoreUnknownKeys = true }

    var users by remember { mutableStateOf<List<String>>(emptyList()) }
    var sales by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }
    var weather by remember { mutableStateOf<List<String>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var time by remember { mutableStateOf(0.0) }

    fun loadData() {
        isLoading = true
        error = null

        scope.launch {
            val elapsed = measureTimeMillis {
                try {
                    // Запускаем все задачи параллельно
                    val usersDeferred = async {
                        delay(1800)
                        val text = context.resources.openRawResource(R.raw.users).bufferedReader().use { it.readText() }
                        json.decodeFromString<List<User>>(text).map { it.name }
                    }

                    val salesDeferred = async {
                        delay(1200)
                        val text = context.resources.openRawResource(R.raw.sales).bufferedReader().use { it.readText() }
                        json.decodeFromString<SalesResponse>(text).items.associate { it.product to it.qty }
                    }

                    val weatherDeferred = async {
                        delay(2500)
                        val text = context.resources.openRawResource(R.raw.weather).bufferedReader().use { it.readText() }
                        json.decodeFromString<List<Weather>>(text).map { "${it.city}: ${it.temp}°C" }
                    }

                    // Ждем все результаты
                    users = usersDeferred.await()
                    sales = salesDeferred.await()
                    weather = weatherDeferred.await()

                } catch (e: Exception) {
                    error = "Ошибка: ${e.message}"
                } finally {
                    isLoading = false
                }
            }
            time = elapsed / 1000.0
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { loadData() },
            enabled = !isLoading
        ) {
            Text(if (isLoading) "Загрузка..." else "Загрузить данные")
        }

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        }

        error?.let {
            Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(8.dp))
        }

        if (users.isNotEmpty() || sales.isNotEmpty() || weather.isNotEmpty()) {
            Text("Время: $time сек", modifier = Modifier.padding(8.dp))

            LazyColumn {
                item {
                    Card(Modifier.fillMaxWidth().padding(4.dp)) {
                        Column(Modifier.padding(8.dp)) {
                            Text("Пользователи", style = MaterialTheme.typography.titleMedium)
                            users.forEach { Text("• $it") }
                        }
                    }
                }

                item {
                    Card(Modifier.fillMaxWidth().padding(4.dp)) {
                        Column(Modifier.padding(8.dp)) {
                            Text("Продажи", style = MaterialTheme.typography.titleMedium)
                            sales.forEach { (product, qty) -> Text("• $product: $qty") }
                        }
                    }
                }

                item {
                    Card(Modifier.fillMaxWidth().padding(4.dp)) {
                        Column(Modifier.padding(8.dp)) {
                            Text("Погода", style = MaterialTheme.typography.titleMedium)
                            weather.forEach { Text("• $it") }
                        }
                    }
                }
            }
        }
    }
}