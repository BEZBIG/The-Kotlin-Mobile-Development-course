package com.example.module4taskspart1

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

// Data классы для задания 1
@Serializable
data class User(
    val id: Int,
    val name: String
)

@Serializable
data class SalesResponse(
    val today: String,
    val items: List<SaleItem>
)

@Serializable
data class SaleItem(
    val product: String,
    val qty: Int,
    val revenue: Int
)

@Serializable
data class Weather(
    val city: String,
    val temp: Int,
    val condition: String
)

// Data классы для задания 2
data class FileInfo(
    val path: String,
    val hash: String
)

// Data классы для задания 3
@Serializable
data class GithubRepo(
    val id: Long,
    @SerialName("full_name")
    val fullName: String,
    val description: String?,
    @SerialName("stargazers_count")
    val stargazersCount: Int,
    val language: String?
)

// Data классы для задания 4
@Serializable
data class SocialPost(
    val id: Int,
    val userId: Int,
    val title: String,
    val body: String,
    val avatarUrl: String
)

@Serializable
data class Comment(
    val postId: Int,
    val id: Int,
    val name: String,
    val body: String
)

// Состояния для UI
data class PostWithDetails(
    val post: SocialPost,
    val comments: List<Comment> = emptyList(),
    val avatarLoaded: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null
)