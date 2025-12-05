// TodoDetailScreenTest.kt
package com.example.todolist

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.todolist.domain.model.TodoItem
import com.example.todolist.presentation.ui.screen.TodoDetailScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TodoDetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testDetailScreenDisplaysTaskInfo() {
        val testTodo = TodoItem(
            id = 1,
            title = "Купить молоко",
            description = "2 литра, обезжиренное",
            isCompleted = false
        )

        composeTestRule.setContent {
            val navController = rememberNavController()
            TodoDetailScreen(todo = testTodo, navController = navController)
        }

        // Проверяем, что отображается заголовок
        composeTestRule.onNodeWithText("Купить молоко").assertIsDisplayed()

        // Проверяем, что отображается описание
        composeTestRule.onNodeWithText("2 литра, обезжиренное").assertIsDisplayed()

        // Проверяем, что отображается статус (с эмодзи)
        // В зависимости от isCompleted будет разный текст
        if (testTodo.isCompleted) {
            composeTestRule.onNodeWithText("✅ Выполнена").assertIsDisplayed()
        } else {
            composeTestRule.onNodeWithText("❌ Не выполнена").assertIsDisplayed()
        }
    }

    @Test
    fun testCompletedTaskDisplaysCorrectStatus() {
        val testTodo = TodoItem(
            id = 2,
            title = "Позвонить маме",
            description = "Спросить про выходные",
            isCompleted = true // Выполнена!
        )

        composeTestRule.setContent {
            val navController = rememberNavController()
            TodoDetailScreen(todo = testTodo, navController = navController)
        }

        // Проверяем, что отображается статус "Выполнена"
        composeTestRule.onNodeWithText("✅ Выполнена").assertIsDisplayed()
    }

    @Test
    fun testBackButtonExists() {
        val testTodo = TodoItem(
            id = 1,
            title = "Test Task",
            description = "Test Description",
            isCompleted = false
        )

        composeTestRule.setContent {
            val navController = rememberNavController()
            TodoDetailScreen(todo = testTodo, navController = navController)
        }

        // Проверяем, что есть кнопка "Назад"
        composeTestRule.onNodeWithText("Назад").assertIsDisplayed()
    }
}