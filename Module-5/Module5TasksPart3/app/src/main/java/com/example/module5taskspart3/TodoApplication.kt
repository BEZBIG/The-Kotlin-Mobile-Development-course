package com.example.module5taskspart3

import android.app.Application
import com.example.module5taskspart3.data.repository.TodoRepositoryImpl
import com.example.module5taskspart3.domain.repository.TodoRepository
import com.example.module5taskspart3.domain.usecase.AddTodoUseCase
import com.example.module5taskspart3.domain.usecase.DeleteTodoUseCase
import com.example.module5taskspart3.domain.usecase.GetTodosUseCase
import com.example.module5taskspart3.domain.usecase.ToggleTodoUseCase
import com.example.module5taskspart3.domain.usecase.UpdateTodoUseCase
import com.example.module5taskspart3.preferences.TodoPreferences

/**
 * Application — точка входа, инициализирует зависимости.
 * Аналог того что было бы в Hilt/Koin, но вручную.
 */
class TodoApplication : Application() {

    lateinit var preferences: TodoPreferences
    lateinit var repository: TodoRepository
    lateinit var getTodosUseCase: GetTodosUseCase
    lateinit var toggleTodoUseCase: ToggleTodoUseCase
    lateinit var addTodoUseCase: AddTodoUseCase
    lateinit var updateTodoUseCase: UpdateTodoUseCase
    lateinit var deleteTodoUseCase: DeleteTodoUseCase

    override fun onCreate() {
        super.onCreate()
        preferences = TodoPreferences(this)
        repository = TodoRepositoryImpl(this, preferences)
        getTodosUseCase = GetTodosUseCase(repository)
        toggleTodoUseCase = ToggleTodoUseCase(repository)
        addTodoUseCase = AddTodoUseCase(repository)
        updateTodoUseCase = UpdateTodoUseCase(repository)
        deleteTodoUseCase = DeleteTodoUseCase(repository)
    }
}