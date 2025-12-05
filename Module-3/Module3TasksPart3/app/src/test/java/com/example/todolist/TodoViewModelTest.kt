package com.example.todolist.presentation.viewmodel

import com.example.todolist.CoroutineTestRule
import com.example.todolist.domain.model.TodoItem
import com.example.todolist.domain.usecase.GetTodosUseCase
import com.example.todolist.domain.usecase.ToggleTodoUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class TodoViewModelTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    @Test
    fun `ViewModel загружает задачи при создании`() = runTest {
        // Arrange
        val mockGetTodosUseCase = mockk<GetTodosUseCase>()
        val mockToggleTodoUseCase = mockk<ToggleTodoUseCase>()

        val fakeTodos = listOf(
            TodoItem(1, "Task 1", "Desc 1", false),
            TodoItem(2, "Task 2", "Desc 2", true)
        )

        coEvery { mockGetTodosUseCase() } returns fakeTodos

        // Act
        val viewModel = TodoViewModel(mockGetTodosUseCase, mockToggleTodoUseCase)

        // Wait for flow to emit
        val todos = viewModel.todos.first()

        // Assert
        assertEquals(2, todos.size)
        assertEquals("Task 1", todos[0].title)
    }

    @Test
    fun `toggleTodo вызывает use case с правильным ID`() = runTest {
        // Arrange
        val mockGetTodosUseCase = mockk<GetTodosUseCase>()
        val mockToggleTodoUseCase = mockk<ToggleTodoUseCase>()

        val fakeTodos = listOf(
            TodoItem(1, "Task 1", "Desc 1", false)
        )

        coEvery { mockGetTodosUseCase() } returns fakeTodos
        coEvery { mockToggleTodoUseCase(any()) } returns Unit // ДОБАВЬТЕ!

        val viewModel = TodoViewModel(mockGetTodosUseCase, mockToggleTodoUseCase)

        // Act
        viewModel.toggleTodo(42)

        // Assert
        coVerify { mockToggleTodoUseCase(42) }
    }
}