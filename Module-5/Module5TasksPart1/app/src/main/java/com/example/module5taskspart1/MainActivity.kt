package com.example.module5taskspart1

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.compose.material.icons.filled.CameraAlt
import android.os.Bundle
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.module5taskspart1.ui.theme.Module5TasksPart1Theme
import kotlinx.coroutines.launch
import java.io.File

// Экраны навигации
sealed class Screen {
    object DiaryList : Screen()
    object DiaryNew : Screen()
    data class DiaryEdit(val fileName: String) : Screen()
    object Gallery : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Module5TasksPart1Theme {
                AppNavigation()
            }
        }
    }
}

/**
 * Простая навигация без Navigation Component —
 * управляем стеком экранов вручную через remember.
 */
@Composable
fun AppNavigation() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.DiaryList) }
    val diaryViewModel: DiaryViewModel = viewModel()
    val galleryViewModel: GalleryViewModel = viewModel()
    val context = LocalContext.current

    // Инициализируем ViewModel-ы один раз
    LaunchedEffect(Unit) {
        diaryViewModel.init(context)
        galleryViewModel.init(context)
    }

    when (val screen = currentScreen) {
        is Screen.DiaryList -> DiaryListScreen(
            viewModel = diaryViewModel,
            onNewEntry = { currentScreen = Screen.DiaryNew },
            onEditEntry = { fileName -> currentScreen = Screen.DiaryEdit(fileName) },
            onOpenGallery = { currentScreen = Screen.Gallery }
        )
        is Screen.DiaryNew -> DiaryEditScreen(
            viewModel = diaryViewModel,
            entry = null,
            onBack = { currentScreen = Screen.DiaryList }
        )
        is Screen.DiaryEdit -> DiaryEditScreen(
            viewModel = diaryViewModel,
            entry = diaryViewModel.readEntry(screen.fileName),
            onBack = { currentScreen = Screen.DiaryList }
        )
        is Screen.Gallery -> GalleryScreen(
            viewModel = galleryViewModel,
            onBack = { currentScreen = Screen.DiaryList }
        )
    }
}

// ─────────────────────────────────────────────────────────────
// Задание 1: Дневник
// ─────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryListScreen(
    viewModel: DiaryViewModel,
    onNewEntry: () -> Unit,
    onEditEntry: (String) -> Unit,
    onOpenGallery: () -> Unit
) {
    val entries by viewModel.entries.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Мой дневник") },
                actions = {
                    IconButton(onClick = onOpenGallery) {
                        Icon(Icons.Default.CameraAlt, contentDescription = "Галерея")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNewEntry) {
                Icon(Icons.Default.Add, contentDescription = "Новая запись")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isLoading) {
                Text(
                    "Загрузка...",
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else if (entries.isEmpty()) {
                // Пустой экран
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("У вас пока нет записей", fontSize = 18.sp, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Нажмите +, чтобы создать первую",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(entries, key = { it.fileName }) { entry ->
                        DiaryEntryCard(
                            entry = entry,
                            onClick = { onEditEntry(entry.fileName) },
                            onDelete = { viewModel.deleteEntry(entry.fileName) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DiaryEntryCard(
    entry: DiaryEntry,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = { showMenu = true }
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = entry.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = entry.dateFormatted,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = entry.preview,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Контекстное меню при долгом нажатии
        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            DropdownMenuItem(
                text = { Text("Удалить", color = Color(0xFFE53935)) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = null,
                        tint = Color(0xFFE53935)
                    )
                },
                onClick = {
                    showMenu = false
                    showDeleteDialog = true
                }
            )
        }
    }

    // Диалог подтверждения удаления
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Удалить запись?") },
            text = { Text("Запись «${entry.title}» будет удалена безвозвратно.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    onDelete()
                }) {
                    Text("Удалить", color = Color(0xFFE53935))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryEditScreen(
    viewModel: DiaryViewModel,
    entry: DiaryEntry?,
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf(entry?.title ?: "") }
    var text by remember { mutableStateOf(entry?.fullText ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (entry == null) "Новая запись" else "Редактировать") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Заголовок (необязательно)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Текст записи") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                maxLines = Int.MAX_VALUE
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    if (entry == null) {
                        viewModel.saveEntry(title, text)
                    } else {
                        viewModel.updateEntry(entry.fileName, title, text)
                    }
                    onBack()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = text.isNotBlank()
            ) {
                Text("Сохранить запись")
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Задание 2: Галерея
// ─────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    viewModel: GalleryViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val photos by viewModel.photos.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Текущий файл, ожидающий результата от камеры
    var pendingPhotoFile by remember { mutableStateOf<File?>(null) }

    // Слушаем одноразовые события (Snackbar)
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is GalleryViewModel.GalleryEvent.ShowMessage ->
                    snackbarHostState.showSnackbar(event.text)
            }
        }
    }

    // Launcher для результата камеры
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            pendingPhotoFile?.let { viewModel.onPhotoCaptured(it) }
        }
        pendingPhotoFile = null
    }

    // Launcher для запроса разрешения камеры
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val result = viewModel.createPhotoUri(context)
            result?.let { (uri, file) ->
                pendingPhotoFile = file
                cameraLauncher.launch(uri)
            }
        }
    }

    fun launchCamera() {
        val hasPerm = ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPerm) {
            val result = viewModel.createPhotoUri(context)
            result?.let { (uri, file) ->
                pendingPhotoFile = file
                cameraLauncher.launch(uri)
            }
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Фотогалерея") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { launchCamera() }) {
                Icon(Icons.Default.CameraAlt, contentDescription = "Сделать фото")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (photos.isEmpty()) {
                // Пустой экран
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "У вас пока нет фото",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { launchCamera() }) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null)
                        Spacer(Modifier.size(8.dp))
                        Text("Сделать первое фото")
                    }
                }
            } else {
                // Сетка фото: 3 колонки
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(photos, key = { it.absolutePath }) { file ->
                        PhotoGridItem(
                            file = file,
                            onExport = { viewModel.exportToGallery(context, file) },
                            onDelete = { viewModel.deletePhoto(file) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PhotoGridItem(
    file: File,
    onExport: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .combinedClickable(
                onClick = {},
                onLongClick = { showMenu = true }
            )
    ) {
        // Загружаем фото из файловой системы через Coil
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(file)
                .crossfade(true)
                .build(),
            contentDescription = file.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Контекстное меню при долгом нажатии
        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            DropdownMenuItem(
                text = { Text("Экспорт в галерею") },
                leadingIcon = {
                    Icon(Icons.Default.Share, contentDescription = null)
                },
                onClick = {
                    showMenu = false
                    onExport()
                }
            )
            DropdownMenuItem(
                text = { Text("Удалить", color = Color(0xFFE53935)) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = null,
                        tint = Color(0xFFE53935)
                    )
                },
                onClick = {
                    showMenu = false
                    onDelete()
                }
            )
        }
    }
}