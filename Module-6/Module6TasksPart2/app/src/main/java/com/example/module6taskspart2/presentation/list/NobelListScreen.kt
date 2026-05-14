package com.example.module6taskspart2.presentation.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.module6taskspart2.domain.model.NobelPrize

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NobelListScreen(
    onItemClick: (NobelPrize) -> Unit,
    viewModel: NobelListViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val year by viewModel.selectedYear.collectAsState()
    val category by viewModel.selectedCategory.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Нобелевские лауреаты") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {

            // Поле для фильтра по году
            OutlinedTextField(
                value = year,
                onValueChange = { viewModel.selectedYear.value = it },
                label = { Text("Год (например 2023)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                singleLine = true
            )

            // Выпадающий список категорий
            CategoryDropdown(
                selected = category,
                onSelected = { viewModel.selectedCategory.value = it }
            )

            // Кнопка применить фильтр
            Button(
                onClick = { viewModel.load() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Text("Применить фильтр")
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when (val s = state) {
                    is NobelListState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    is NobelListState.Error -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(s.message)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { viewModel.load() }) { Text("Повторить") }
                        }
                    }
                    is NobelListState.Success -> {
                        LazyColumn {
                            items(s.prizes) { prize ->
                                NobelPrizeCard(prize = prize, onClick = { onItemClick(prize) })
                            }
                        }
                    }
                }
            }
        }
    }
}

// Карточка одного лауреата
@Composable
fun NobelPrizeCard(prize: NobelPrize, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "${prize.year} — ${prize.category}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = prize.laureateName,
                style = MaterialTheme.typography.titleMedium
            )
            if (prize.motivation.isNotBlank()) {
                Text(
                    text = prize.motivation.take(100),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

// Выпадающий список для выбора категории
@Composable
fun CategoryDropdown(selected: String, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val label = if (selected.isBlank()) "Все категории" else selected

    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(label)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            categories.forEach { cat ->
                DropdownMenuItem(
                    text = { Text(if (cat.isBlank()) "Все категории" else cat) },
                    onClick = {
                        onSelected(cat)
                        expanded = false
                    }
                )
            }
        }
    }
}