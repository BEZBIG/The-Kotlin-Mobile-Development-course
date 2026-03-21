package com.example.module5taskspart3.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity — таблица "todos" в SQLite.
 * Структура полей та же что у TodoItem из Модуля 3.
 */
@Entity(tableName = "todos")
data class TodoItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val isCompleted: Boolean = false
)