package com.example.module4taskspart6

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.module4taskspart6.ui.theme.Module4TasksPart6Theme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Module4TasksPart6Theme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Flow в Jetpack Compose") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Задание 12: Cold Flow
            Task12AnimalFacts()

            HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp))

            // Задание 13: StateFlow
            Task13CurrencyRate()

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Задание 12: Cold Flow — Генератор фактов о животных
// ─────────────────────────────────────────────────────────────

@Composable
fun Task12AnimalFacts(
    viewModel: AnimalFactsViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()

    // Текущий факт — null означает что ещё ничего не загружалось
    var currentFact by remember { mutableStateOf<String?>(null) }

    // Флаг загрузки — показываем спиннер пока Flow выполняется
    var isLoading by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Text(
            text = "Задание 12: Cold Flow",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Генератор фактов о животных",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Карточка с фактом или заглушка
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            // Центрируем содержимое карточки
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                when {
                    // Показываем спиннер пока идёт загрузка
                    isLoading -> {
                        CircularProgressIndicator(modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Ищем интересный факт...",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Показываем факт с анимацией появления
                    currentFact != null -> {
                        AnimatedVisibility(
                            visible = !isLoading,
                            enter = fadeIn() + slideInVertically(),
                            exit = fadeOut()
                        ) {
                            Text(
                                text = currentFact!!,
                                fontSize = 15.sp,
                                lineHeight = 22.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // Начальное состояние — подсказка
                    else -> {
                        Text(
                            text = "Нажмите кнопку, чтобы\nузнать интересный факт!",
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Кнопка запускает новый collect — cold Flow стартует заново
        Button(
            onClick = {
                scope.launch {
                    isLoading = true
                    currentFact = null

                    // Каждый collect() запускает Flow заново с начала
                    // Это и есть главная особенность cold Flow
                    viewModel.getRandomFact().collect { fact ->
                        currentFact = fact
                        isLoading = false
                    }
                }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = if (isLoading) "Загружаем..." else "Новый факт!")
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Задание 13: StateFlow — Живой курс валют
// ─────────────────────────────────────────────────────────────

@Composable
fun Task13CurrencyRate(
    viewModel: CurrencyViewModel = viewModel()
) {
    // collectAsStateWithLifecycle — автоматически отменяет подписку
    // когда экран уходит в фон, это лучшая практика для Compose
    val rate by viewModel.rate.collectAsStateWithLifecycle()
    val previousRate by viewModel.previousRate.collectAsStateWithLifecycle()
    val lastUpdated by viewModel.lastUpdated.collectAsStateWithLifecycle()

    // Определяем направление изменения курса
    val isRateUp = rate.compareTo(previousRate) > 0
    val isRateDown = rate.compareTo(previousRate) < 0
    val rateChanged = rate.compareTo(previousRate) != 0

    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Text(
            text = "Задание 13: StateFlow",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Живой курс USD / RUB",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Карточка с курсом
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Символ валюты
                Text(
                    text = "1 USD",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Курс + стрелка изменения
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Большой текст с курсом
                    Text(
                        text = "%.2f ₽".format(rate),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Стрелка показывает направление изменения
                    if (rateChanged) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isRateUp) "▲" else "▼",
                            fontSize = 28.sp,
                            color = if (isRateUp) Color(0xFF43A047) else Color(0xFFE53935)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Изменение в рублях
                if (rateChanged) {
                    val delta = rate - previousRate
                    val deltaText = if (delta > 0) "+%.2f".format(delta)
                    else "%.2f".format(delta)
                    Text(
                        text = "$deltaText ₽",
                        fontSize = 16.sp,
                        color = if (isRateUp) Color(0xFF43A047) else Color(0xFFE53935)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                HorizontalDivider()

                Spacer(modifier = Modifier.height(12.dp))

                // Время последнего обновления
                Text(
                    text = lastUpdated,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Автообновление каждые 5 секунд",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Кнопка принудительного обновления
        Button(
            onClick = { viewModel.generateNewRate() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1976D2)
            )
        ) {
            Text(
                text = "Обновить сейчас",
                fontSize = 16.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}
