package com.example.module5taskspart2

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * ViewModel галереи.
 * Фото хранятся в getExternalFilesDir(DIRECTORY_PICTURES) —
 * приватная папка, не требует разрешений.
 * Экспорт в общую галерею — через MediaStore (без разрешений на Android 10+).
 */
class GalleryViewModel : ViewModel() {

    private val _photos = MutableStateFlow<List<File>>(emptyList())
    val photos: StateFlow<List<File>> = _photos.asStateFlow()

    // SharedFlow для одноразовых событий (Snackbar сообщения)
    private val _events = MutableSharedFlow<GalleryEvent>()
    val events: SharedFlow<GalleryEvent> = _events.asSharedFlow()

    private var picturesDir: File? = null

    sealed class GalleryEvent {
        data class ShowMessage(val text: String) : GalleryEvent()
    }

    /**
     * Инициализация: передаём context один раз, сканируем папку.
     */
    fun init(context: Context) {
        if (picturesDir != null) return
        picturesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        loadPhotos()
    }

    /**
     * Сканирует папку и загружает список фото (новые первые).
     */
    private fun loadPhotos() {
        viewModelScope.launch {
            val files = withContext(Dispatchers.IO) {
                picturesDir?.listFiles { file ->
                    file.extension.lowercase() in listOf("jpg", "jpeg", "png")
                }
                    ?.sortedByDescending { it.lastModified() }
                    ?: emptyList()
            }
            _photos.value = files
        }
    }

    /**
     * Создаёт файл для нового фото и возвращает URI для камеры.
     * Имя файла: IMG_yyyyMMdd_HHmmss.jpg
     */
    fun createPhotoUri(context: Context): Pair<Uri, File>? {
        val dir = picturesDir ?: return null
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            .format(Date())
        val file = File(dir, "IMG_${timestamp}.jpg")
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        return Pair(uri, file)
    }

    /**
     * Добавляет новое фото в начало списка после съёмки.
     */
    fun onPhotoCaptured(file: File) {
        if (file.exists() && file.length() > 0) {
            _photos.value = listOf(file) + _photos.value
        }
    }

    /**
     * Экспортирует фото в общую галерею через MediaStore.
     *
     * Алгоритм:
     * 1. Создаём запись в MediaStore с IS_PENDING=1 (файл "в процессе")
     * 2. Копируем байты из приватного файла в OutputStream от MediaStore
     * 3. Снимаем IS_PENDING=0 — файл становится видим в галерее
     *
     * Не требует никаких разрешений на Android 10+.
     */
    fun exportToGallery(context: Context, file: File) {
        viewModelScope.launch {
            val success = withContext(Dispatchers.IO) {
                try {
                    val contentValues = ContentValues().apply {
                        put(MediaStore.Images.Media.DISPLAY_NAME, file.name)
                        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                        // RELATIVE_PATH — папка внутри Pictures куда попадёт файл
                        put(
                            MediaStore.Images.Media.RELATIVE_PATH,
                            Environment.DIRECTORY_PICTURES + "/Module5Gallery"
                        )
                        // IS_PENDING=1 — файл ещё записывается, галерея его не показывает
                        put(MediaStore.Images.Media.IS_PENDING, 1)
                    }

                    // Создаём запись в MediaStore и получаем URI
                    val uri = context.contentResolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        contentValues
                    ) ?: return@withContext false

                    // Копируем содержимое файла через OutputStream
                    context.contentResolver.openOutputStream(uri)?.use { output ->
                        file.inputStream().use { input ->
                            input.copyTo(output)
                        }
                    }

                    // IS_PENDING=0 — файл готов, галерея его видит
                    contentValues.clear()
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                    context.contentResolver.update(uri, contentValues, null, null)

                    true
                } catch (e: Exception) {
                    false
                }
            }

            _events.emit(
                GalleryEvent.ShowMessage(
                    if (success) "Фото добавлено в галерею" else "Ошибка экспорта"
                )
            )
        }
    }

    /**
     * Удаляет фото из приватной папки приложения.
     */
    fun deletePhoto(file: File) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { file.delete() }
            _photos.value = _photos.value.filter { it.absolutePath != file.absolutePath }
        }
    }
}