package com.example.module6taskspart3.presentation.users

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.module6taskspart3.data.local.TokenStorage
import com.example.module6taskspart3.domain.model.User
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersListScreen(
    tokenStorage: TokenStorage,
    onUserClick: (User) -> Unit,
    viewModel: UsersListViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()

    // Загружаем пользователей при первом открытии экрана
    LaunchedEffect(Unit) {
        val token = tokenStorage.tokenFlow.first() ?: ""
        viewModel.loadUsers(token)
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Пользователи") }) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val s = state) {
                is UsersState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is UsersState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(s.message)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = {
                            scope.launch {
                                val token = tokenStorage.tokenFlow.first() ?: ""
                                viewModel.loadUsers(token)
                            }
                        }) { Text("Повторить") }
                    }
                }
                is UsersState.Success -> {
                    LazyColumn {
                        items(s.users) { user ->
                            UserCard(user = user, onClick = { onUserClick(user) })
                        }
                    }
                }
            }
        }
    }
}

// Карточка одного пользователя
@Composable
fun UserCard(user: User, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = user.image,
                contentDescription = user.username,
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "${user.firstName} ${user.lastName}",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(text = "@${user.username}", style = MaterialTheme.typography.bodySmall)
                Text(text = user.email, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}