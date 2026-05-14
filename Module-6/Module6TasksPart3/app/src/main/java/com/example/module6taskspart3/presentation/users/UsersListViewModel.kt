package com.example.module6taskspart3.presentation.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.module6taskspart3.data.repository.UserRepositoryImpl
import com.example.module6taskspart3.domain.model.User
import com.example.module6taskspart3.domain.usecase.GetUsersUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class UsersState {
    object Loading : UsersState()
    data class Success(val users: List<User>) : UsersState()
    data class Error(val message: String) : UsersState()
}

class UsersListViewModel : ViewModel() {

    private val getUsersUseCase = GetUsersUseCase(UserRepositoryImpl())

    private val _state = MutableStateFlow<UsersState>(UsersState.Loading)
    val state: StateFlow<UsersState> = _state

    // Загружает список пользователей по токену
    fun loadUsers(token: String) {
        viewModelScope.launch {
            _state.value = UsersState.Loading
            try {
                val users = getUsersUseCase(token)
                _state.value = UsersState.Success(users)
            } catch (e: Exception) {
                _state.value = UsersState.Error(e.message ?: "Ошибка загрузки")
            }
        }
    }
}