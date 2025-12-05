package com.example.todolist.domain.usecase

import com.example.todolist.domain.repository.TodoRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ToggleTodoUseCaseTest {
    @Test
    fun `toggleTodo меняет isCompleted`() = runTest {
        val mockRepository = mockk<TodoRepository>()
        coEvery { mockRepository.toggleTodo(any()) } returns Unit
        val useCase = ToggleTodoUseCase(mockRepository)

        useCase(1)

        coVerify { mockRepository.toggleTodo(1) }
    }
}