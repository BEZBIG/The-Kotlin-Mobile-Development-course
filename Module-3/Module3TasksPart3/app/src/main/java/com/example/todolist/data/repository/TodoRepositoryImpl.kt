package com.example.todolist.data.repository

import android.content.Context
import com.example.todolist.data.local.TodoJsonDataSource
import com.example.todolist.data.model.TodoItemDto
import com.example.todolist.domain.model.TodoItem
import com.example.todolist.domain.repository.TodoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TodoRepositoryImpl(context: Context) : TodoRepository {
    private val dataSource = TodoJsonDataSource(context)
    private val _todos = MutableStateFlow<List<TodoItem>>(emptyList())

    init {
        loadTodos()
    }

    private fun loadTodos() {
        val dtos = dataSource.getTodos()
        _todos.value = dtos.map { dto ->
            TodoItem(
                id = dto.id,
                title = dto.title,
                description = dto.description,
                isCompleted = dto.isCompleted
            )
        }
    }

    override suspend fun getTodos(): List<TodoItem> {
        return _todos.value
    }

    override suspend fun toggleTodo(id: Int) {
        _todos.value = _todos.value.map { item ->
            if (item.id == id) {
                item.copy(isCompleted = !item.isCompleted)
            } else {
                item
            }
        }
    }
}