package com.example.module5taskspart3.data.repository

import android.content.Context
import com.example.module5taskspart3.data.local.TodoDatabase
import com.example.module5taskspart3.data.local.TodoJsonDataSource
import com.example.module5taskspart3.data.model.TodoItemEntity
import com.example.module5taskspart3.domain.model.TodoItem
import com.example.module5taskspart3.domain.repository.TodoRepository
import com.example.module5taskspart3.preferences.TodoPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import android.util.Log
import kotlinx.coroutines.flow.map

/**
 * Реализация репозитория.
 * Объединяет Room (БД) и TodoJsonDataSource (начальные данные из JSON).
 * Аналог TodoRepositoryImpl из Модуля 3, но теперь с Room вместо in-memory.
 */
class TodoRepositoryImpl(
    private val context: Context,
    private val preferences: TodoPreferences
) : TodoRepository {

    private val dao = TodoDatabase.getDatabase(context).todoDao()
    private val jsonSource = TodoJsonDataSource(context)

    override fun getTodos(): Flow<List<TodoItem>> {
        return dao.getAll().map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun addTodo(todo: TodoItem) {
        dao.insert(todo.toEntity())
    }

    override suspend fun updateTodo(todo: TodoItem) {
        dao.update(todo.toEntity())
    }

    override suspend fun deleteTodo(todo: TodoItem) {
        dao.delete(todo.toEntity())
    }

    override suspend fun importFromJsonIfNeeded() {
        // Проверяем только по количеству в БД — игнорируем DataStore флаг
        // это надёжнее чем флаг который мог сохраниться при сбое
        val count = dao.getCount()
        Log.d("TodoImport", "Current DB count: $count")

        if (count > 0) return

        val dtos = jsonSource.loadTodos()
        Log.d("TodoImport", "Loaded ${dtos.size} items from JSON")

        if (dtos.isEmpty()) return

        val entities = dtos.map { dto ->
            TodoItemEntity(
                title = dto.title,
                description = dto.description,
                isCompleted = dto.isCompleted
            )
        }

        dao.insertAll(entities)
        Log.d("TodoImport", "Inserted ${entities.size} items into Room")
        preferences.markJsonImported()
    }



    private fun TodoItemEntity.toDomain() = TodoItem(
        id = id, title = title, description = description, isCompleted = isCompleted
    )

    private fun TodoItem.toEntity() = TodoItemEntity(
        id = id, title = title, description = description, isCompleted = isCompleted
    )
}