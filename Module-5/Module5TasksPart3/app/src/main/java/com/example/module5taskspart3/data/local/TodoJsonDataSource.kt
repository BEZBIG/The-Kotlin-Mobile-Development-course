package com.example.module5taskspart3.data.local

import android.content.Context
import android.util.Log
import com.example.module5taskspart3.data.model.TodoItemDto
import org.json.JSONArray

/**
 * Источник данных из JSON-файла assets/todos.json.
 * Тот же класс что был в Модуле 3 — читает начальные задачи.
 */
class TodoJsonDataSource(private val context: Context) {

    fun loadTodos(): List<TodoItemDto> {
        return try {
            // Проверяем что файл вообще существует в assets
            val files = context.assets.list("") ?: emptyArray()
            Log.d("TodoJson", "Assets files: ${files.toList()}")

            val json = context.assets
                .open("todos.json")
                .bufferedReader()
                .readText()

            Log.d("TodoJson", "JSON content: $json")

            val jsonArray = JSONArray(json)
            Log.d("TodoJson", "JSON array length: ${jsonArray.length()}")

            List(jsonArray.length()) { i ->
                TodoItemDto.fromJson(jsonArray.getJSONObject(i))
            }.also { list ->
                Log.d("TodoJson", "Parsed ${list.size} todos")
            }
        } catch (e: Exception) {
            Log.e("TodoJson", "Error loading todos.json", e)
            emptyList()
        }
    }
}