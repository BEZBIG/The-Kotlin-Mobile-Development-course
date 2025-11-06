package com.example.module_2_tasks_part_1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Задание 1
                        Hello("Никита")
                        Hello(null)

                        // Задание 3
                        JetpackTextVariants()

                        // Задание 4
                        CustomButton()

                        // Задание 5
                        ProfileWithInitials()
                    }
                }
            }
        }
    }
}

// ЗАДАНИЕ 1: Функция Hello с обработкой null
@Composable
fun Hello(name: String?, modifier: Modifier = Modifier) {
    Text(
        text = if (name == null) "Имя не задано" else "Привет, $name!",
        modifier = modifier
    )
}

// ЗАДАНИЕ 2: Preview функции Hello в разных ориентациях
@Preview(showBackground = true, device = "spec:width=411dp,height=891dp,dpi=420")
@Composable
fun HelloPreviewPortrait() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Hello("Никита Портрет", modifier = Modifier.padding(16.dp))
        }
    }
}

@Preview(showBackground = true, device = "spec:width=891dp,height=411dp,dpi=420")
@Composable
fun HelloPreviewLandscape() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Hello("Никита Ландшафт", modifier = Modifier.padding(16.dp))
        }
    }
}

@Preview(showBackground = true, widthDp = 200, heightDp = 200)
@Composable
fun HelloPreviewRound() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .background(Color.Yellow),
            contentAlignment = Alignment.Center
        ) {
            Hello("Никита Круглый", modifier = Modifier.padding(16.dp))
        }
    }
}

// ЗАДАНИЕ 3: Текст с разными стилями
@Composable
fun JetpackTextVariants() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Вариант 1: Зеленый, курсив, по центру
        Text(
            text = stringResource(R.string.jetpack_description),
            color = Color.Green,
            fontSize = 16.sp,
            fontStyle = FontStyle.Italic,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        // Вариант 2: Одна строка с многоточием
        Text(
            text = stringResource(R.string.jetpack_description),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )

        // Вариант 3: Черный текст на зеленом фоне, подчеркнутый
        Text(
            text = stringResource(R.string.jetpack_description),
            fontSize = 24.sp,
            color = Color.Black,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier
                .background(Color.Green)
                .padding(start = 48.dp, top = 16.dp, bottom = 16.dp, end = 16.dp)
                .fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun JetpackTextPreview() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            JetpackTextVariants()
        }
    }
}

// ЗАДАНИЕ 4: Кастомная кнопка
@Composable
fun CustomButton() {
    Button(
        onClick = { /* Действие при нажатии */ },
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.LightGray,
            contentColor = Color.Black
        ),
        border = BorderStroke(
            width = 2.dp,
            color = Color.Gray
        )
    ) {
        Text(
            text = "Нажми на меня",
            fontSize = 16.sp,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CustomButtonPreview() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            CustomButton()
        }
    }
}

// ЗАДАНИЕ 5: Профиль с инициалами
@Composable
fun ProfileWithInitials() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(Color.Gray)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.2f)
                .clip(CircleShape)
                .background(Color.Blue)
                .align(Alignment.Center)
        ) {
            Text(
                text = "НБ",
                color = Color.White,
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileWithInitialsPreview() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            ProfileWithInitials()
        }
    }
}

// Общий Preview для всех заданий
@Preview(showBackground = true)
@Composable
fun AllTasksPreview() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Hello("Никита")
                Hello(null)
                JetpackTextVariants()
                CustomButton()
                ProfileWithInitials()
            }
        }
    }
}