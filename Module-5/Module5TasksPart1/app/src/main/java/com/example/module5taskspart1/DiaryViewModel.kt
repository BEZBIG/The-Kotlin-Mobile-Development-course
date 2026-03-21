package com.example.module5taskspart1

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Модель одной записи дневника.
 * fileName — уникальный ключ записи (имя файла на диске).
 */
data class DiaryEntry(
    val fileName: String,
    val title: String,
    val preview: String,   // первые 40 символов текста
    val fullText: String,
    val dateFormatted: String
)

/**
 * ViewModel для дневника.
 *
 * Ключевое требование: НЕ перезагружать весь список при каждой операции.
 * - init: одно полное сканирование папки при запуске
 * - сохранение: добавляем новую запись в начало списка вручную
 * - удаление: убираем запись из списка по fileName без сканирования
 */
class DiaryViewModel : ViewModel() {

    private val _entries = MutableStateFlow<List<DiaryEntry>>(emptyList())
    val entries: StateFlow<List<DiaryEntry>> = _entries.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private lateinit var filesDir: File

    /**
     * Инициализация: передаём context один раз из UI.
     * Запускаем полное сканирование папки — только здесь и только раз.
     */
    fun init(context: Context) {
        if (::filesDir.isInitialized) return
        filesDir = context.filesDir
        loadAllEntries()
    }

    /**
     * Полное сканирование папки — вызывается только один раз при запуске.
     */
    private fun loadAllEntries() {
        viewModelScope.launch {
            _isLoading.value = true
            val loaded = withContext(Dispatchers.IO) {
                filesDir.listFiles { file ->
                    file.name.endsWith(".txt") && file.name.startsWith("diary_")
                }
                    ?.sortedByDescending { it.lastModified() }
                    ?.map { file -> parseFile(file) }
                    ?: emptyList()
            }
            _entries.value = loaded
            _isLoading.value = false
        }
    }

    /**
     * Сохраняет новую запись и добавляет её в начало списка.
     * Папку НЕ перечитываем — просто вставляем в начало.
     */
    fun saveEntry(title: String, text: String) {
        viewModelScope.launch {
            val entry = withContext(Dispatchers.IO) {
                val timestamp = System.currentTimeMillis()
                val safeName = title
                    .take(20)
                    .replace(Regex("[^a-zA-Zа-яА-Я0-9]"), "_")
                    .ifEmpty { "note" }
                val fileName = "diary_${timestamp}_${safeName}.txt"
                val file = File(filesDir, fileName)

                // Формат файла: первая строка — заголовок, остальное — текст
                val content = "${title.ifEmpty { "Без заголовка" }}\n$text"
                file.writeText(content, Charsets.UTF_8)
                parseFile(file)
            }
            // Добавляем в начало без пересканирования
            _entries.value = listOf(entry) + _entries.value
        }
    }

    /**
     * Удаляет запись с диска и убирает из списка по fileName.
     * Папку НЕ перечитываем.
     */
    fun deleteEntry(fileName: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                File(filesDir, fileName).delete()
            }
            // Убираем из списка по ключу без пересканирования
            _entries.value = _entries.value.filter { it.fileName != fileName }
        }
    }

    /**
     * Читает содержимое записи с диска для экрана редактирования.
     */
    fun readEntry(fileName: String): DiaryEntry? {
        return _entries.value.find { it.fileName == fileName }
    }

    /**
     * Обновляет существующую запись на диске и в списке.
     */
    fun updateEntry(fileName: String, title: String, text: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val file = File(filesDir, fileName)
                val content = "${title.ifEmpty { "Без заголовка" }}\n$text"
                file.writeText(content, Charsets.UTF_8)
            }
            // Обновляем запись в списке без пересканирования
            val updated = File(filesDir, fileName).let { parseFile(it) }
            _entries.value = _entries.value.map {
                if (it.fileName == fileName) updated else it
            }
        }
    }

    /**
     * Парсит файл дневника в объект DiaryEntry.
     */
    private fun parseFile(file: File): DiaryEntry {
        val lines = file.readLines(Charsets.UTF_8)
        val title = lines.firstOrNull() ?: "Без заголовка"
        val text = lines.drop(1).joinToString("\n")
        val preview = text.take(40).let { if (text.length > 40) "$it..." else it }
        val date = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            .format(Date(file.lastModified()))
        return DiaryEntry(
            fileName = file.name,
            title = title,
            preview = preview.ifEmpty { "Пустая запись" },
            fullText = text,
            dateFormatted = date
        )
    }
}