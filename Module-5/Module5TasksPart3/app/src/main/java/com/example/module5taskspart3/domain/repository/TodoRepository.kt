package com.example.module5taskspart3.domain.repository

import com.example.module5taskspart3.domain.model.TodoItem
import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    fun getTodos(): Flow<List<TodoItem>>
    suspend fun addTodo(todo: TodoItem)
    suspend fun updateTodo(todo: TodoItem)
    suspend fun deleteTodo(todo: TodoItem)
    // Однократный импорт из JSON в Room при первом запуске
    suspend fun importFromJsonIfNeeded()
}