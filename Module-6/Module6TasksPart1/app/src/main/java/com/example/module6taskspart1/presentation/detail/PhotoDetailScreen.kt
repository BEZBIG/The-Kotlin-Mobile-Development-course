package com.example.module6taskspart1.presentation.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.module6taskspart1.domain.model.Photo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoDetailScreen(
    photo: Photo,
    onBack: () -> Unit,
    viewModel: PhotoDetailViewModel = viewModel()
) {
    val context = LocalContext.current
    val downloadState by viewModel.downloadState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(downloadState) {
        when (downloadState) {
            is DownloadState.Success -> {
                snackbarHostState.showSnackbar("Фото сохранено в Downloads!")
                viewModel.resetDownloadState()
            }
            is DownloadState.Error -> {
                snackbarHostState.showSnackbar("Ошибка: ${(downloadState as DownloadState.Error).message}")
                viewModel.resetDownloadState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Детали фото") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Большая картинка
            AsyncImage(
                model = photo.downloadUrl,
                contentDescription = photo.author,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(text = "Автор: ${photo.author}", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Размеры: ${photo.width} × ${photo.height}")
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Ссылка: ${photo.url}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Кнопка скачать
                Button(
                    onClick = {
                        viewModel.downloadPhoto(context, photo.downloadUrl, "photo_${photo.id}")
                    },
                    enabled = downloadState !is DownloadState.Downloading,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    if (downloadState is DownloadState.Downloading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text("Скачать фото")
                }
            }
        }
    }
}