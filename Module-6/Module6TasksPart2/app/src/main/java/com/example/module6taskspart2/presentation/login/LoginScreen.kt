package com.example.module6taskspart2.presentation.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.module6taskspart2.data.local.TokenStorage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class LoginReq(val username: String, val password: String)

@Serializable
data class LoginResp(val token: String = "")

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}

class LoginViewModel(private val tokenStorage: TokenStorage) : ViewModel() {

    private val client = HttpClient(Android) {
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
    }

    private val _state = MutableStateFlow<LoginState>(LoginState.Idle)
    val state: StateFlow<LoginState> = _state

    // Логинимся на нашем сервере и сохраняем токен
    fun login(username: String, password: String) {
        viewModelScope.launch {
            _state.value = LoginState.Loading
            try {
                val response = client.post("http://10.0.2.2:8080/login") {
                    contentType(ContentType.Application.Json)
                    setBody(LoginReq(username, password))
                }.body<LoginResp>()
                tokenStorage.saveToken(response.token)
                _state.value = LoginState.Success
            } catch (e: Exception) {
                _state.value = LoginState.Error("Ошибка: ${e.message}")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    tokenStorage: TokenStorage,
    onLoginSuccess: () -> Unit
) {
    val viewModel = remember { LoginViewModel(tokenStorage) }
    val state by viewModel.state.collectAsState()
    var username by remember { mutableStateOf("admin") }
    var password by remember { mutableStateOf("password123") }

    LaunchedEffect(state) {
        if (state is LoginState.Success) onLoginSuccess()
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Вход") }) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Логин") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Пароль") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))
            if (state is LoginState.Error) {
                Text((state as LoginState.Error).message, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
            }
            Button(
                onClick = { viewModel.login(username, password) },
                enabled = state !is LoginState.Loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state is LoginState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else Text("Войти")
            }
        }
    }
}