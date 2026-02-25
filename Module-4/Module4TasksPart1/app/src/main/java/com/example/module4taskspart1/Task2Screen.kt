package com.example.module4taskspart1

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import java.io.File
import java.security.MessageDigest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Task2Screen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(false) }
    var duplicateGroups by remember { mutableStateOf<List<List<String>>>(emptyList()) }
    var timeoutMessage by remember { mutableStateOf<String?>(null) }

    // Функция для вычисления SHA-256 хеша файла
    suspend fun calculateFileHash(file: File): String {
        return withContext(Dispatchers.IO) {
            try {
                val digest = MessageDigest.getInstance("SHA-256")
                val bytes = file.readBytes()
                val hashBytes = digest.digest(bytes)

                // Конвертируем в hex строку
                hashBytes.joinToString("") { "%02x".format(it) }
            } catch (e: Exception) {
                "ERROR_${file.name}"
            }
        }
    }

    // Поиск всех JSON файлов в директории
    fun findJsonFiles(directory: File): List<File> {
        return directory.walk()
            .filter { it.isFile && it.extension.lowercase() == "json" }
            .toList()
    }

    // Копируем JSON файлы из raw в директорию приложения для поиска
    fun copyJsonFilesToAppDir(context: Context, appDir: File) {
        val rawFiles = listOf(
            R.raw.users to "users.json",
            R.raw.sales to "sales.json",
            R.raw.weather to "weather.json",
            R.raw.github_repos to "github_repos.json",
            R.raw.social_posts to "social_posts.json",
            R.raw.comments to "comments.json"
        )

        rawFiles.forEach { (rawId, fileName) ->
            val destFile = File(appDir, fileName)
            if (!destFile.exists()) {
                context.resources.openRawResource(rawId).use { input ->
                    destFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            }
        }

        // Создаем дубликат для демонстрации
        val sourceFile = File(appDir, "users.json")
        val duplicateFile = File(appDir, "users_copy.json")
        if (sourceFile.exists() && !duplicateFile.exists()) {
            sourceFile.copyTo(duplicateFile)
        }
    }

    fun startSearch() {
        isLoading = true
        timeoutMessage = null

        scope.launch {
            // Устанавливаем таймаут 3 секунды для поиска
            val result = withTimeoutOrNull(3000L) {
                try {
                    // Получаем директорию приложения
                    val appDir = context.filesDir

                    // Для демонстрации копируем JSON файлы в директорию приложения
                    copyJsonFilesToAppDir(context, appDir)

                    // Ищем все JSON файлы
                    val jsonFiles = findJsonFiles(appDir)

                    if (jsonFiles.isEmpty()) {
                        emptyList()
                    } else {
                        // Запускаем параллельное вычисление хешей
                        val hashDeferred = jsonFiles.map { file ->
                            async {
                                FileInfo(file.path, calculateFileHash(file))
                            }
                        }

                        val fileInfos = hashDeferred.awaitAll()

                        // Группируем по хешу и находим дубликаты
                        fileInfos
                            .groupBy { it.hash }
                            .filter { it.value.size > 1 } // Только дубликаты
                            .map { it.value.map { info -> info.path } }
                    }
                } catch (e: CancellationException) {
                    emptyList()
                }
            }

            if (result == null) {
                timeoutMessage = "Поиск прерван по таймауту (3 секунды)"
                duplicateGroups = emptyList()
            } else {
                duplicateGroups = result
            }

            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { startSearch() },
            enabled = !isLoading,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(if (isLoading) "Поиск..." else "Найти дубликаты JSON файлов")
        }

        Text(
            text = "Поиск с таймаутом 3 секунды",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (isLoading) {
            CircularProgressIndicator()
        }

        if (timeoutMessage != null) {
            Card(
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(
                    text = timeoutMessage!!,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        if (duplicateGroups.isNotEmpty()) {
            Text(
                text = "Найдены группы дубликатов:",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            LazyColumn {
                items(duplicateGroups) { group ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Группа дубликатов (${group.size} файлов):",
                                style = MaterialTheme.typography.titleMedium
                            )

                            group.forEach { path ->
                                Text(
                                    text = "• ${File(path).name}",
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        } else if (!isLoading && timeoutMessage == null) {
            Text(text = "Дубликаты не найдены")
        }
    }
}