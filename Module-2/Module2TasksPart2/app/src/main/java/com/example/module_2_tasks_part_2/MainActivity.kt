package com.example.module_2_tasks_part_2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
                        // ЗАДАНИЕ 6
                        //CircleInTopRight()
                       // CircleInCenterStretched()

                        // ЗАДАНИЕ 7
                        //ColumnLayout1()
                        //ColumnLayout2()
                        //RowLayout()

                        // ЗАДАНИЕ 8
                        WaterTracker()
                    }
                }
            }
        }
    }
}

// ЗАДАНИЕ 6: Vector Image и расположение
@Composable
fun CircleInTopRight() {
    Box(
        modifier = Modifier
            .size(240.dp, 120.dp)
            .background(Color.Black)
    ) {
        androidx.compose.foundation.Image(
            painter = painterResource(R.drawable.circle),
            contentDescription = "Круг в правом верхнем углу",
            modifier = Modifier
                .align(Alignment.TopEnd)
        )
    }
}

@Composable
fun CircleInCenterStretched() {
    Box(
        modifier = Modifier
            .size(240.dp, 120.dp)
            .background(Color.Blue)
    ) {
        androidx.compose.foundation.Image(
            painter = painterResource(R.drawable.circle),
            contentDescription = "Круг по центру растянутый",
            modifier = Modifier
                .fillMaxSize(),
            colorFilter = ColorFilter.tint(Color.Magenta)
        )
    }
}

// ЗАДАНИЕ 7: Column и Row layouts
@Composable
fun ColumnLayout1() {
    Column(
        modifier = Modifier
            .background(Color.LightGray)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Элемент  1", modifier = Modifier.background(Color.Red).padding(8.dp))
        Text("Элемент 2", modifier = Modifier.background(Color.Green).padding(8.dp))
        Text("Элемент 3", modifier = Modifier.background(Color.Blue).padding(8.dp))
    }
}

@Composable
fun ColumnLayout2() {
    Column(
        modifier = Modifier
            .background(Color.LightGray)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Центрированный 1", modifier = Modifier.background(Color.Yellow).padding(8.dp))
        Text("Центрированный 2", modifier = Modifier.background(Color.Cyan).padding(8.dp))
        Text("Центрированный 3", modifier = Modifier.background(Color.Magenta).padding(8.dp))
    }
}

@Composable
fun RowLayout() {
    Row(
        modifier = Modifier
            .background(Color.LightGray)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Левый", modifier = Modifier.background(Color.Red).padding(8.dp))
        Text("Центр", modifier = Modifier.background(Color.Green).padding(8.dp))
        Text("Правый", modifier = Modifier.background(Color.Blue).padding(8.dp))
    }
}

// ЗАДАНИЕ 8: Water Tracker
@Composable
fun WaterTracker() {
    var waterCount by remember { mutableIntStateOf(100) }
    var streakDays by remember { mutableIntStateOf(0) }

    val primaryColor = Color(0xFF2196F3)
    val secondaryColor = Color(0xFF1976D2)
    val buttonTextColor = Color.White

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Text(
            text = "Трекер воды",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = primaryColor
        )

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "${waterCount} мл",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = secondaryColor
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Кнопка добавления воды
        Button(
            colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
            onClick = { waterCount += 250 },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "+250 мл",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = buttonTextColor
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Кнопка завершения дня
        Button(
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
            onClick = {
                if (waterCount >= 1500) {
                    streakDays++
                } else {
                    streakDays = 0
                }
                waterCount = 0
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Завершить день",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = buttonTextColor
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Отображение счетчика дней
        Text(
            text = "Дней подряд: $streakDays",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = primaryColor
        )

        // Подсказка
        Text(
            text = "Цель: 1500 мл в день",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

// Preview функции
@Preview(showBackground = true)
@Composable
fun Task6Preview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircleInTopRight()
            CircleInCenterStretched()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Task7Preview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ColumnLayout1()
            ColumnLayout2()
            RowLayout()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Task8Preview() {
    MaterialTheme {
        WaterTracker()
    }
}

@Preview(showBackground = true)
@Composable
fun AllTasksPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircleInTopRight()
            CircleInCenterStretched()
            ColumnLayout1()
            ColumnLayout2()
            RowLayout()
            WaterTracker()
        }
    }
}