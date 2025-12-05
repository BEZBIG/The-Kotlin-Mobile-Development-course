package com.example.module3taskspart2.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class CounterViewModel : ViewModel() {

    val counter = mutableStateOf(0)

    fun increment() {
        counter.value++
    }
}
