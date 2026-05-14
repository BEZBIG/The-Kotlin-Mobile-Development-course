package com.example.module6taskspart2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.module6taskspart2.presentation.navigation.AppNavigation
import com.example.module6taskspart2.ui.theme.Module6TasksPart2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Module6TasksPart2Theme {
                AppNavigation()
            }
        }
    }
}