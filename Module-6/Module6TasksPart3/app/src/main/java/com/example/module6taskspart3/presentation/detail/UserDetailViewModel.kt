package com.example.module6taskspart3.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.module6taskspart3.data.local.TokenStorage
import com.example.module6taskspart3.data.repository.UserRepositoryImpl
import com.example.module6taskspart3.domain.model.User
import com.example.module6taskspart3.domain.usecase.GetUserByIdUseCase
import com.example.module6taskspart3.domain.usecase.LogoutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class UserDetailState {
    object Loading : UserDetailState()
    data class Success(val user: User) : UserDetailState()
    data class Error(val message: String) : UserDetailState()
}

class UserDetailViewModel(private val tokenStorage: TokenStorage) : ViewModel() {

    private val getUserByIdUseCase = GetUserByIdUseCase(UserRepositoryImpl())
    private val logoutUseCase = LogoutUseCase(tokenStorage)

    private val _state = MutableStateFlow<UserDetailState>(UserDetailState.Loading)
    val state: StateFlow<UserDetailState> = _state

    // Загружает детали пользователя
    fun loadUser(id: Int, token: String) {
        viewModelScope.launch {
            _state.value = UserDetailState.Loading
            try {
                val user = getUserByIdUseCase(id, token)
                _state.value = UserDetailState.Success(user)
            } catch (e: Exception) {
                _state.value = UserDetailState.Error(e.message ?: "Ошибка")
            }
        }
    }

    // Выходит из аккаунта — удаляет токен
    fun logout(onDone: () -> Unit) {
        viewModelScope.launch {
            logoutUseCase()
            onDone()
        }
    }
}