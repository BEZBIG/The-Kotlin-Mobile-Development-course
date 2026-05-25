package com.example.module6taskspart2.presentation.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.module6taskspart2.data.local.TokenStorage
import com.example.module6taskspart2.domain.model.NobelPrize
import com.example.module6taskspart2.presentation.detail.NobelDetailScreen
import com.example.module6taskspart2.presentation.list.NobelListScreen
import com.example.module6taskspart2.presentation.login.LoginScreen

object Routes {
    const val LOGIN = "login"
    const val LIST = "list"
    const val DETAIL = "detail"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val tokenStorage = remember { TokenStorage(context) }
    var selectedPrize: NobelPrize? = null

    NavHost(navController = navController, startDestination = Routes.LOGIN) {

        composable(Routes.LOGIN) {
            LoginScreen(
                tokenStorage = tokenStorage,
                onLoginSuccess = {
                    navController.navigate(Routes.LIST) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.LIST) {
            NobelListScreen(
                onItemClick = { prize ->
                    selectedPrize = prize
                    navController.navigate(Routes.DETAIL)
                }
            )
        }

        composable(Routes.DETAIL) {
            val prize = selectedPrize ?: return@composable
            NobelDetailScreen(
                prize = prize,
                onBack = { navController.popBackStack() }
            )
        }
    }
}