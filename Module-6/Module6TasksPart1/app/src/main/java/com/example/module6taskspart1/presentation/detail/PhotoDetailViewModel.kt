package com.example.module6taskspart1.presentation.detail

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.URL


sealed class DownloadState {
    object Idle : DownloadState()
    object Downloading : DownloadState()
    object Success : DownloadState()
    data class Error(val message: String) : DownloadState()
}

class PhotoDetailViewModel : ViewModel() {

    private val _downloadState = MutableStateFlow<DownloadState>(DownloadState.Idle)
    val downloadState: StateFlow<DownloadState> = _downloadState
    fun downloadPhoto(context: Context, downloadUrl: String, fileName: String) {
        viewModelScope.launch {
            _downloadState.value = DownloadState.Downloading
            try {
                withContext(Dispatchers.IO) {
                    val inputStream: InputStream = URL(downloadUrl).openStream()
                    val bytes = inputStream.readBytes()
                    inputStream.close()

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                        val contentValues = ContentValues().apply {
                            put(MediaStore.Images.Media.DISPLAY_NAME, "$fileName.jpg")
                            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                        }
                        val uri = context.contentResolver.insert(
                            MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                            contentValues
                        )
                        uri?.let { context.contentResolver.openOutputStream(it)?.use { os -> os.write(bytes) } }
                    } else {

                        val file = java.io.File(
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                            "$fileName.jpg"
                        )
                        file.writeBytes(bytes)
                    }
                }
                _downloadState.value = DownloadState.Success
            } catch (e: Exception) {
                _downloadState.value = DownloadState.Error(e.message ?: "Ошибка скачивания")
            }
        }
    }

    // Сбрасываем состояние
    fun resetDownloadState() {
        _downloadState.value = DownloadState.Idle
    }
}