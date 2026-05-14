package com.example.module6taskspart2.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.module6taskspart2.data.repository.NobelRepositoryImpl
import com.example.module6taskspart2.domain.model.NobelPrize
import com.example.module6taskspart2.domain.usecase.GetNobelPrizesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

val categories = listOf("", "physics", "chemistry", "literature", "peace", "medicine", "economics")

sealed class NobelListState {
    object Loading : NobelListState()
    data class Success(val prizes: List<NobelPrize>) : NobelListState()
    data class Error(val message: String) : NobelListState()
}

class NobelListViewModel : ViewModel() {

    private val useCase = GetNobelPrizesUseCase(NobelRepositoryImpl())

    private val _state = MutableStateFlow<NobelListState>(NobelListState.Loading)
    val state: StateFlow<NobelListState> = _state

    // Текущие значения фильтров
    var selectedYear = MutableStateFlow("")
    var selectedCategory = MutableStateFlow("")

    init {
        load()
    }

    // Загружает список с текущими фильтрами
    fun load() {
        viewModelScope.launch {
            _state.value = NobelListState.Loading
            try {
                val year = selectedYear.value.ifBlank { null }
                val category = selectedCategory.value.ifBlank { null }
                val prizes = useCase(year, category)
                _state.value = NobelListState.Success(prizes)
            } catch (e: Exception) {
                _state.value = NobelListState.Error(e.message ?: "Ошибка загрузки")
            }
        }
    }
}