package com.example.module6taskspart1.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.module6taskspart1.data.network.RetrofitClient
import com.example.module6taskspart1.data.repository.PhotoRepositoryImpl
import com.example.module6taskspart1.domain.model.Photo
import com.example.module6taskspart1.domain.usecase.GetPhotosUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Состояния экрана
sealed class PhotoListState {
    object Loading : PhotoListState()
    data class Success(val photos: List<Photo>) : PhotoListState()
    data class Error(val message: String) : PhotoListState()
}

class PhotoListViewModel : ViewModel() {

    // Создаём зависимости
    private val repository = PhotoRepositoryImpl(RetrofitClient.photoApi)
    private val getPhotosUseCase = GetPhotosUseCase(repository)

    private val _state = MutableStateFlow<PhotoListState>(PhotoListState.Loading)
    val state: StateFlow<PhotoListState> = _state

    init {
        loadPhotos()
    }

    // Загружаем фотографии
    fun loadPhotos() {
        viewModelScope.launch {
            _state.value = PhotoListState.Loading
            try {
                val photos = getPhotosUseCase()
                _state.value = PhotoListState.Success(photos)
            } catch (e: Exception) {
                _state.value = PhotoListState.Error(e.message ?: "Неизвестная ошибка")
            }
        }
    }
}