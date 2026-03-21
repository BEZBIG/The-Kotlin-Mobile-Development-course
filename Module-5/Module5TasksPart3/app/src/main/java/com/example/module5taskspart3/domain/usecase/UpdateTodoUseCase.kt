package com.example.module5taskspart3.domain.usecase

import com.example.module5taskspart3.domain.model.TodoItem
import com.example.module5taskspart3.domain.repository.TodoRepository

class UpdateTodoUseCase(private val repository: TodoRepository) {
    suspend operator fun invoke(todo: TodoItem) = repository.updateTodo(todo)
}