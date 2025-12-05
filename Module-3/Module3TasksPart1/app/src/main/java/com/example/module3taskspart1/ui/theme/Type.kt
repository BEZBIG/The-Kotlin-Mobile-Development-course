package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.module3taskspart1.R



val ExoFontFamily = FontFamily(
    Font(R.font.exo2r, FontWeight.Normal),
    Font(R.font.exo2m, FontWeight.Medium),
    Font(R.font.exo2b, FontWeight.Bold)
)

val AppTypography = Typography(
    bodyLarge = TextStyle(
        fontFamily = ExoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    titleLarge = TextStyle(
        fontFamily = ExoFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp
    ),
    labelSmall = TextStyle(
        fontFamily = ExoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp
    )
)

