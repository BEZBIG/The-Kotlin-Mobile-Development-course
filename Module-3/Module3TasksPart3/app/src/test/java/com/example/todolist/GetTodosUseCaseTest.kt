package com.example.todolist.domain.usecase

import com.example.todolist.domain.model.TodoItem
import com.example.todolist.domain.repository.TodoRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class GetTodosUseCaseTest {
    @Test
    fun `GetTodosUseCase возвращает 3 задачи`() = runTest {
        val mockRepository = mockk<TodoRepository>()
        val fakeTodos = listOf(
            TodoItem(1, "Купить молоко", "2 литра, обезжиренное", false),
            TodoItem(2, "Позвонить маме", "Спросить про выходные", true),
            TodoItem(3, "Сделать ДЗ по Android", "Clean Architecture + Compose", false)
        )

        coEvery { mockRepository.getTodos() } returns fakeTodos
        val useCase = GetTodosUseCase(mockRepository)

        val result = useCase()

        assertEquals(3, result.size)
        assertEquals("Купить молоко", result[0].title)
        assertTrue(result[1].isCompleted)
        assertFalse(result[0].isCompleted)
    }
}