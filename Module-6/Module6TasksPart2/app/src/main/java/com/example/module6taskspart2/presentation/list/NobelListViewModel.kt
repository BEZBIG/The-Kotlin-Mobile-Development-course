package com.example.module6taskspart2.presentation.list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.module6taskspart2.data.local.TokenStorage
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

class NobelListViewModel(application: Application) : AndroidViewModel(application) {

    private val tokenStorage = TokenStorage(application)
    private val useCase = GetNobelPrizesUseCase(NobelRepositoryImpl(tokenStorage))

    private val _state = MutableStateFlow<NobelListState>(NobelListState.Loading)
    val state: StateFlow<NobelListState> = _state

    var selectedYear = MutableStateFlow("")
    var selectedCategory = MutableStateFlow("")

    init {
        load()
    }

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