package com.example.module6taskspart7

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.module6taskspart7.presentation.MainScreen
import com.example.module6taskspart7.ui.theme.Module6TasksPart7Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Module6TasksPart7Theme {
                MainScreen()
            }
        }
    }
}