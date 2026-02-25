package com.example.module4taskspart1

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*
import kotlinx.coroutines.async
import kotlinx.serialization.json.Json

// Состояние поста с деталями
data class PostState(
    val post: SocialPost,
    val comments: List<Comment> = emptyList(),
    val avatarLoaded: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Task4Screen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val json = Json { ignoreUnknownKeys = true }

    // Состояния
    var postStates by remember { mutableStateOf<List<PostState>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Job для управления всеми загрузками
    var loadingJob by remember { mutableStateOf<Job?>(null) }

    // Загрузка комментариев для поста
    suspend fun loadCommentsForPost(postId: Int): List<Comment> = withContext(Dispatchers.IO) {
        delay(800 + (postId * 100L)) // Имитация сети
        val jsonString = context.resources.openRawResource(R.raw.comments)
            .bufferedReader().use { it.readText() }
        val allComments = json.decodeFromString<List<Comment>>(jsonString)
        allComments.filter { it.postId == postId }
    }

    // Загрузка аватарки (имитация)
    suspend fun loadAvatar(postId: Int): Boolean {
        delay(600 + (postId * 50L)) // Имитация сети
        // Случайный успех/ошибка (80% успех)
        return (1..10).random() <= 8
    }

    // Загрузка всех данных
    fun loadAllPosts() {
        // Отменяем предыдущую загрузку
        loadingJob?.cancel()

        isLoading = true
        postStates = emptyList()

        loadingJob = scope.launch {
            try {
                // 1. Загружаем посты (имитация сети)
                val posts = withContext(Dispatchers.IO) {
                    delay(500)
                    val jsonString = context.resources.openRawResource(R.raw.social_posts)
                        .bufferedReader().use { it.readText() }
                    json.decodeFromString<List<SocialPost>>(jsonString)
                }

                // Создаем начальные состояния для всех постов
                val initialStates = posts.map { PostState(post = it) }
                withContext(Dispatchers.Main) {
                    postStates = initialStates
                }

                // 2. Для каждого поста параллельно загружаем комментарии и аватарку
                val jobs = mutableListOf<Job>()

                posts.forEachIndexed { index, post ->
                    val job = scope.launch {
                        try {
                            // Параллельная загрузка внутри coroutineScope
                            coroutineScope {
                                val commentsDeferred = async {
                                    loadCommentsForPost(post.id)
                                }

                                val avatarDeferred = async {
                                    loadAvatar(post.id)
                                }

                                // Ждем оба результата
                                val comments = commentsDeferred.await()
                                val avatarSuccess = avatarDeferred.await()

                                // Обновляем состояние конкретного поста
                                withContext(Dispatchers.Main) {
                                    val currentStates = postStates.toMutableList()
                                    if (index < currentStates.size) {
                                        currentStates[index] = PostState(
                                            post = post,
                                            comments = comments,
                                            avatarLoaded = avatarSuccess,
                                            isLoading = false,
                                            error = if (!avatarSuccess) "Ошибка аватарки" else null
                                        )
                                        postStates = currentStates
                                    }
                                }
                            }
                        } catch (e: CancellationException) {
                            // Загрузка отменена - ничего не делаем
                            throw e
                        } catch (e: Exception) {
                            // Ошибка загрузки
                            withContext(Dispatchers.Main) {
                                val currentStates = postStates.toMutableList()
                                if (index < currentStates.size) {
                                    currentStates[index] = PostState(
                                        post = post,
                                        isLoading = false,
                                        error = "Ошибка: ${e.message}"
                                    )
                                    postStates = currentStates
                                }
                            }
                        }
                    }
                    jobs.add(job)
                }

                // Ждем завершения всех загрузок или отмены
                try {
                    jobs.forEach { it.join() }
                } catch (e: CancellationException) {
                    // Загрузка отменена
                }

            } catch (e: Exception) {
                // Ошибка загрузки постов
                println("Error loading posts: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    // Загрузка при старте
    LaunchedEffect(Unit) {
        loadAllPosts()
    }

    // Функция обновления
    fun refresh() {
        loadAllPosts()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Верхняя панель с кнопкой обновления
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Социальная лента", style = MaterialTheme.typography.headlineSmall)

            Button(
                onClick = { refresh() },
                enabled = !isLoading
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Обновить")
                Spacer(Modifier.width(8.dp))
                Text("Обновить")
            }
        }

        Spacer(Modifier.height(8.dp))

        // Индикатор общей загрузки
        if (isLoading && postStates.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Text("Загрузка постов...", modifier = Modifier.padding(8.dp))
                }
            }
        }

        // Список постов
        LazyColumn {
            items(postStates) { postState ->
                PostCardWithState(postState = postState)
            }
        }
    }
}

@Composable
fun PostCardWithState(postState: PostState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Заголовок с аватаркой
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Аватарка с состоянием загрузки
                when {
                    postState.isLoading -> {
                        // Состояние загрузки
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color.LightGray, CircleShape)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(20.dp)
                                    .align(Alignment.Center)
                            )
                        }
                    }
                    postState.avatarLoaded -> {
                        // Аватарка загружена - показываем цветной кружок
                        // Преобразуем Int в Float правильно
                        val hue = (postState.post.id * 30) % 360
                        val color = Color.hsv(
                            hue = hue.toFloat(),  // Явное преобразование в Float
                            saturation = 0.7f,
                            value = 0.9f
                        )

                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(color, CircleShape)
                        )
                    }
                    else -> {
                        // Ошибка загрузки аватарки
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color.Red.copy(alpha = 0.3f), CircleShape)
                        ) {
                            Text(
                                text = "!",
                                modifier = Modifier.align(Alignment.Center),
                                color = Color.Red
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = postState.post.title,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Текст поста
            Text(
                text = postState.post.body,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Комментарии с состоянием загрузки
            when {
                postState.isLoading -> {
                    // Загрузка комментариев
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Загрузка комментариев...")
                    }
                }
                postState.error != null && postState.comments.isEmpty() -> {
                    // Ошибка загрузки
                    Text(
                        text = postState.error!!,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                postState.comments.isNotEmpty() -> {
                    // Комментарии загружены
                    Text(
                        text = "Комментарии (${postState.comments.size}):",
                        style = MaterialTheme.typography.titleSmall
                    )

                    postState.comments.take(3).forEach { comment ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Text(
                                    text = comment.name,
                                    style = MaterialTheme.typography.labelSmall
                                )
                                Text(
                                    text = comment.body,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }

                    if (postState.comments.size > 3) {
                        Text(
                            text = "и еще ${postState.comments.size - 3} комментариев...",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
                else -> {
                    // Нет комментариев
                    Text(
                        text = "Нет комментариев",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}