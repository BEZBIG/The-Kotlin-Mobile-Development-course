package com.example.module5taskspart3.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.module5taskspart3.TodoApplication
import com.example.module5taskspart3.domain.model.TodoItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TodoViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as TodoApplication

    val todos: StateFlow<List<TodoItem>> = app.getTodosUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val completedColor: StateFlow<String> = app.preferences.completedColor
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = "green"
        )

    init {
        viewModelScope.launch {
            // importFromJsonIfNeeded — suspend функция, ждём завершения.
            // После того как она вставит данные в Room,
            // dao.getAll() автоматически выдаст новые значения через Flow —
            // и StateFlow выше обновится сам.
            app.repository.importFromJsonIfNeeded()
        }
    }

    fun toggleTodo(todo: TodoItem) {
        viewModelScope.launch { app.toggleTodoUseCase(todo) }
    }

    fun addTodo(title: String, description: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            app.addTodoUseCase(TodoItem(title = title, description = description))
        }
    }

    fun updateTodo(todo: TodoItem, newTitle: String, newDescription: String) {
        viewModelScope.launch {
            app.updateTodoUseCase(todo.copy(title = newTitle, description = newDescription))
        }
    }

    fun deleteTodo(todo: TodoItem) {
        viewModelScope.launch { app.deleteTodoUseCase(todo) }
    }

    fun saveCompletedColor(color: String) {
        viewModelScope.launch { app.preferences.saveCompletedColor(color) }
    }
}