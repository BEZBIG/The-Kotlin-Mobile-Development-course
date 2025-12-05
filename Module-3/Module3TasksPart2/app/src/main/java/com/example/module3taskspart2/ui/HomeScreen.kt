package com.example.module3taskspart2.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.module3taskspart2.navigation.DetailsScreen
import com.example.module3taskspart2.viewmodel.CounterViewModel

@Composable
fun HomeScreen(navController: NavController, counterVM: CounterViewModel = viewModel()) {

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            "Счётчик: ${counterVM.counter.value}",
            style = MaterialTheme.typography.headlineMedium
        )

        Button(
            modifier = Modifier.padding(top = 16.dp),
            onClick = { counterVM.increment() }
        ) {
            Text("Увеличить счётчик")
        }

        Button(
            modifier = Modifier.padding(top = 16.dp),
            onClick = { navController.navigate(DetailsScreen) }
        ) {
            Text("Перейти на Details")
        }
    }
}
