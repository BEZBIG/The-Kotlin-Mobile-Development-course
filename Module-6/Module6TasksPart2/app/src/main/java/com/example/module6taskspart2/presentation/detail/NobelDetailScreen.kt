package com.example.module6taskspart2.presentation.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.module6taskspart2.domain.model.NobelPrize

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NobelDetailScreen(prize: NobelPrize, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Детали") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(text = prize.laureateName, style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Год: ${prize.year}")
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Категория: ${prize.category}")
            Spacer(modifier = Modifier.height(4.dp))
            if (prize.country.isNotBlank()) {
                Text(text = "Страна: ${prize.country}")
                Spacer(modifier = Modifier.height(4.dp))
            }
            if (prize.motivation.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Описание:", style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = prize.motivation, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}