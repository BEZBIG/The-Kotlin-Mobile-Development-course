package com.example.module3taskspart1.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val AppShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(
        topStart = 12.dp,
        topEnd = 20.dp,
        bottomStart = 20.dp,
        bottomEnd = 12.dp
    ),
    large = RoundedCornerShape(30.dp)
)
