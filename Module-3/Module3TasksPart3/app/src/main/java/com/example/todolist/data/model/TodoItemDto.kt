package com.example.todolist.data.model

import com.google.gson.annotations.SerializedName

data class TodoItemDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("isCompleted")
    val isCompleted: Boolean
)