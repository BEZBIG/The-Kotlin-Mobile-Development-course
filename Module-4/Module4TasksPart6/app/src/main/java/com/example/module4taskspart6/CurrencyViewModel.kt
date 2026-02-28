package com.example.module4taskspart6

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Задание 13: ViewModel для живого курса валют.
 * Использует MutableStateFlow — горячий поток, который:
 * - Хранит последнее значение (не теряется при повороте экрана)
 * - Новый подписчик сразу получает актуальный курс
 */
class CurrencyViewModel : ViewModel() {

    // Базовый курс доллара к рублю (центральное значение)
    private val baseRate = 90.0

    // MutableStateFlow хранит текущий курс.
    // Приватный — изменять может только ViewModel.
    private val _rate = MutableStateFlow(baseRate)

    // Публичный StateFlow — только для чтения из UI.
    val rate: StateFlow<Double> = _rate.asStateFlow()

    // Предыдущий курс — нужен чтобы определить стрелку вверх/вниз
    private val _previousRate = MutableStateFlow(baseRate)
    val previousRate: StateFlow<Double> = _previousRate.asStateFlow()

    // Время последнего обновления
    private val _lastUpdated = MutableStateFlow("Инициализация...")
    val lastUpdated: StateFlow<String> = _lastUpdated.asStateFlow()

    init {
        // Запускаем автоматическое обновление курса каждые 5 секунд
        startAutoUpdate()
    }

    /**
     * Запускает корутину в viewModelScope — она живёт пока жива ViewModel.
     * Каждые 5 секунд генерирует новый случайный курс.
     */
    private fun startAutoUpdate() {
        viewModelScope.launch {
            while (true) {
                delay(5000L) // ждём 5 секунд
                generateNewRate()
            }
        }
    }

    /**
     * Генерирует новый курс: базовый ± случайное отклонение до 2.0 рублей.
     * Обновляет StateFlow — UI автоматически получит новое значение.
     */
    fun generateNewRate() {
        // Сохраняем предыдущий курс для отображения стрелки
        _previousRate.value = _rate.value

        // Новый курс: случайное отклонение от -2.0 до +2.0
        val delta = ((-200..200).random()) / 100.0
        val newRate = (baseRate + delta).coerceIn(85.0, 95.0)

        // Обновляем StateFlow — все подписчики получат новое значение мгновенно
        _rate.value = newRate

        // Обновляем время последнего обновления
        val now = java.util.Calendar.getInstance()
        _lastUpdated.value = String.format(
            "Обновлено в %02d:%02d:%02d",
            now.get(java.util.Calendar.HOUR_OF_DAY),
            now.get(java.util.Calendar.MINUTE),
            now.get(java.util.Calendar.SECOND)
        )
    }
}