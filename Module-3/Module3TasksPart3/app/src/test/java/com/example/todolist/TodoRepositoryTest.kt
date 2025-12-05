package com.example.todolist.data.repository

import com.example.todolist.CoroutineTestRule
import com.example.todolist.domain.model.TodoItem
import com.example.todolist.domain.repository.TodoRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class TodoRepositoryTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    @Test
    fun `getTodos возвращает список задач`() = runTest {
        // Arrange
        val mockRepository = mockk<TodoRepository>()
        val expectedTodos = listOf(
            TodoItem(1, "Task 1", "Description 1", false),
            TodoItem(2, "Task 2", "Description 2", true),
            TodoItem(3, "Task 3", "Description 3", false)
        )

        coEvery { mockRepository.getTodos() } returns expectedTodos

        // Act
        val result = mockRepository.getTodos()

        // Assert
        assertEquals(3, result.size)
        assertEquals(expectedTodos, result)
    }

    @Test
    fun `toggleTodo вызывается с правильным ID`() = runTest {
        // Arrange
        val mockRepository = mockk<TodoRepository>()
        coEvery { mockRepository.toggleTodo(any()) } returns Unit // ДОБАВЬТЕ ЭТУ СТРОКУ!
        val taskId = 42

        // Act
        mockRepository.toggleTodo(taskId)

        // Assert
        coVerify { mockRepository.toggleTodo(taskId) }
    }

    @Test
    fun `getTodos возвращает пустой список когда нет данных`() = runTest {
        // Arrange
        val mockRepository = mockk<TodoRepository>()
        coEvery { mockRepository.getTodos() } returns emptyList()

        // Act
        val result = mockRepository.getTodos()

        // Assert
        assertTrue(result.isEmpty())
    }
}