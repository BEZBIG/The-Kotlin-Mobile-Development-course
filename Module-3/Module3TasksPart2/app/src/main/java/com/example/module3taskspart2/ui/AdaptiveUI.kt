package com.example.module3taskspart2.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun AdaptiveUI(
    windowSize: WindowWidthSizeClass,
    navController: NavController
) {
    when (windowSize) {

        WindowWidthSizeClass.Compact -> {
            HomeScreen(navController)
        }

        WindowWidthSizeClass.Medium,
        WindowWidthSizeClass.Expanded -> {

            Row(modifier = Modifier.fillMaxSize()) {

                Box(
                    modifier = Modifier.weight(1f).fillMaxHeight().padding(8.dp)
                ) {
                    Text("Sidebar")
                }

                Box(
                    modifier = Modifier.weight(2f)
                ) {
                    HomeScreen(navController)
                }
            }
        }
    }
}
