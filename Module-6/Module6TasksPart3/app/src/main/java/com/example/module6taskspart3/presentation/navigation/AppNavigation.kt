package com.example.module6taskspart3.presentation.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.module6taskspart3.data.local.TokenStorage
import com.example.module6taskspart3.domain.model.User
import com.example.module6taskspart3.presentation.detail.UserDetailScreen
import com.example.module6taskspart3.presentation.detail.UserDetailViewModel
import com.example.module6taskspart3.presentation.login.LoginScreen
import com.example.module6taskspart3.presentation.login.LoginViewModel
import com.example.module6taskspart3.presentation.users.UsersListScreen

object Routes {
    const val LOGIN = "login"
    const val USERS = "users"
    const val DETAIL = "detail"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val tokenStorage = remember { TokenStorage(context) }

    var selectedUser: User? = null

    NavHost(navController = navController, startDestination = Routes.LOGIN) {

        composable(Routes.LOGIN) {
            val viewModel = remember { LoginViewModel(tokenStorage) }
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = {
                    navController.navigate(Routes.USERS) {
                        // Убираем логин из стека чтобы нельзя было вернуться назад
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.USERS) {
            UsersListScreen(
                tokenStorage = tokenStorage,
                onUserClick = { user ->
                    selectedUser = user
                    navController.navigate(Routes.DETAIL)
                }
            )
        }

        composable(Routes.DETAIL) {
            val user = selectedUser ?: return@composable
            val viewModel = remember { UserDetailViewModel(tokenStorage) }
            UserDetailScreen(
                userId = user.id,
                tokenStorage = tokenStorage,
                onBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                viewModel = viewModel
            )
        }
    }
}