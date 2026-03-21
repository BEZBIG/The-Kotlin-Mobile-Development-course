package com.example.module5taskspart3.domain.usecase

import com.example.module5taskspart3.domain.repository.TodoRepository
import com.example.module5taskspart3.domain.model.TodoItem

// Тот же use case что в Модуле 3 — переключает isCompleted
class ToggleTodoUseCase(private val repository: TodoRepository) {
    suspend operator fun invoke(todo: TodoItem) {
        repository.updateTodo(todo.copy(isCompleted = !todo.isCompleted))
    }
}