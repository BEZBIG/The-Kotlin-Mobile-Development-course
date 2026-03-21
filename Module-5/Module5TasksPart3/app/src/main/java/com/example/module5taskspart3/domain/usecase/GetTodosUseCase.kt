package com.example.module5taskspart3.domain.usecase

import com.example.module5taskspart3.domain.model.TodoItem
import com.example.module5taskspart3.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow

class GetTodosUseCase(private val repository: TodoRepository) {
    operator fun invoke(): Flow<List<TodoItem>> = repository.getTodos()
}