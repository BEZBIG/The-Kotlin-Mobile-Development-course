package com.example.module6taskspart3.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.module6taskspart3.data.local.TokenStorage
import com.example.module6taskspart3.data.repository.UserRepositoryImpl
import com.example.module6taskspart3.domain.usecase.LoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}

class LoginViewModel(private val tokenStorage: TokenStorage) : ViewModel() {

    private val loginUseCase = LoginUseCase(UserRepositoryImpl())

    private val _state = MutableStateFlow<LoginState>(LoginState.Idle)
    val state: StateFlow<LoginState> = _state

    // Выполняет логин и сохраняет токен
    fun login(username: String, password: String) {
        viewModelScope.launch {
            _state.value = LoginState.Loading
            try {
                val token = loginUseCase(username, password)
                tokenStorage.saveToken(token)
                _state.value = LoginState.Success
            } catch (e: Exception) {
                _state.value = LoginState.Error(e.message ?: "Неверные данные")
            }
        }
    }
}