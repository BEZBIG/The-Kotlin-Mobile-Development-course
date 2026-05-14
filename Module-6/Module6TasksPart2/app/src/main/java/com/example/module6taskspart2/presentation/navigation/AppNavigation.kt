package com.example.module6taskspart2.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.module6taskspart2.domain.model.NobelPrize
import com.example.module6taskspart2.presentation.detail.NobelDetailScreen
import com.example.module6taskspart2.presentation.list.NobelListScreen

object Routes {
    const val LIST = "list"
    const val DETAIL = "detail"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    var selectedPrize: NobelPrize? = null

    NavHost(navController = navController, startDestination = Routes.LIST) {
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