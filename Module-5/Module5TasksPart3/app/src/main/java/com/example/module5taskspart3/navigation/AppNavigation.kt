package com.example.module5taskspart3.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.module5taskspart3.domain.model.TodoItem
import com.example.module5taskspart3.presentation.ui.screen.TodoDetailScreen
import com.example.module5taskspart3.presentation.ui.screen.TodoListScreen

sealed class Screen {
    object List : Screen()
    data class Detail(val todo: TodoItem) : Screen()
}

@Composable
fun AppNavigation() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.List) }

    when (val screen = currentScreen) {
        is Screen.List -> TodoListScreen(
            // Передаём колбэк — при нажатии на карточку переходим на Detail
            onTodoClick = { todo -> currentScreen = Screen.Detail(todo) }
        )
        is Screen.Detail -> TodoDetailScreen(
            todo = screen.todo,
            onBack = { currentScreen = Screen.List }
        )
    }
}