package com.example.module5taskspart3.data.model

import org.json.JSONObject

/**
 * DTO — Data Transfer Object для парсинга todos.json.
 */
data class TodoItemDto(
    val id: Int,
    val title: String,
    val description: String,
    val isCompleted: Boolean
) {
    companion object {
        fun fromJson(json: JSONObject) = TodoItemDto(
            id = json.getInt("id"),
            title = json.getString("title"),
            description = json.getString("description"),
            isCompleted = json.getBoolean("isCompleted")
        )
    }
}