package com.example.module3taskspart2.navigation

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.module3taskspart2.ui.AdaptiveUI
import com.example.module3taskspart2.ui.DetailsScreen

@Composable
fun AppNavGraph(windowSize: WindowWidthSizeClass) {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = HomeScreen
    ) {

        composable<HomeScreen> {
            AdaptiveUI(windowSize, navController)
        }

        composable<DetailsScreen> {
            DetailsScreen()
        }
    }
}
