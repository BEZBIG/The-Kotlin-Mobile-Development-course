package com.example.module4taskspart1

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Task3Screen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val json = Json { ignoreUnknownKeys = true }

    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<GithubRepo>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    // Функция для загрузки и фильтрации репозиториев
    suspend fun searchRepositories(query: String): List<GithubRepo> {
        // Имитация задержки сети
        delay(800)

        return withContext(Dispatchers.IO) {
            try {
                // Загружаем JSON из ресурсов
                val jsonString = context.resources.openRawResource(R.raw.github_repos)
                    .bufferedReader().use { it.readText() }

                val allRepos = json.decodeFromString<List<GithubRepo>>(jsonString)

                // Фильтруем по запросу
                if (query.isBlank()) {
                    emptyList()
                } else {
                    allRepos.filter { repo ->
                        repo.fullName.contains(query, ignoreCase = true) ||
                                (repo.description?.contains(query, ignoreCase = true) == true)
                    }
                }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    // Реализация debounce функции
    fun <T> debounce(
        waitMs: Long = 500L,
        destinationFunction: (T) -> Unit
    ): (T) -> Unit {
        var debounceJob: Job? = null

        return { param: T ->
            debounceJob?.cancel()
            debounceJob = scope.launch {
                delay(waitMs)
                destinationFunction(param)
            }
        }
    }

    // Создаем debounced функцию поиска
    val debouncedSearch = remember {
        debounce<String>(
            waitMs = 500L,
            destinationFunction = { query ->
                scope.launch {
                    isLoading = true
                    searchResults = searchRepositories(query)
                    isLoading = false
                }
            }
        )
    }

    // Обработчик изменения текста поиска
    fun onSearchQueryChanged(query: String) {
        searchQuery = query
        debouncedSearch(query)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Поле поиска
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { onSearchQueryChanged(it) },
            label = { Text("Поиск репозиториев...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Индикатор загрузки
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Результаты поиска
        if (searchResults.isNotEmpty() || !isLoading) {
            Text(
                text = "Найдено репозиториев: ${searchResults.size}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            LazyColumn {
                items(searchResults) { repo ->
                    RepositoryCard(repo = repo)
                }
            }
        }
    }
}

@Composable
fun RepositoryCard(repo: GithubRepo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = repo.fullName,
                style = MaterialTheme.typography.titleMedium
            )

            repo.description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                repo.language?.let {
                    Text(
                        text = "Язык: $it",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Text(
                    text = "⭐ ${repo.stargazersCount}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}