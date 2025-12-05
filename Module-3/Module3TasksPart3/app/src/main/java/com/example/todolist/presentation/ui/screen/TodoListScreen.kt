// TodoListScreen.kt
package com.example.todolist.presentation.ui.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.todolist.domain.model.TodoItem
import com.example.todolist.presentation.ui.component.TodoItemCard
import com.example.todolist.presentation.viewmodel.TodoViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun TodoListScreen(
    viewModel: TodoViewModel,
    navController: NavController
) {
    val todos = viewModel.todos.value

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Список задач",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(todos) { todo ->
                TodoItemCard(
                    todo = todo,
                    onToggle = { viewModel.toggleTodo(todo.id) },
                    onClick = { navController.navigate("detail/${todo.id}") }
                )
            }
        }
    }
}