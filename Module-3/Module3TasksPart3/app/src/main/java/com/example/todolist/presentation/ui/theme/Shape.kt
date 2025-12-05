package com.example.todolist.presentation.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val TodoShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

// Custom shapes
val TodoCardShape = RoundedCornerShape(12.dp)
val TodoButtonShape = RoundedCornerShape(8.dp)
val TodoTextFieldShape = RoundedCornerShape(8.dp)