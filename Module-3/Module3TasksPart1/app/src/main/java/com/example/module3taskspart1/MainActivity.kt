package com.example.module3taskspart1

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.module3taskspart1.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CardList()
                }
            }
        }
    }
}

// -------------------------
// ЗАДАНИЕ 1 и 2: LazyColumn + Card
// -------------------------

data class CardItem(
    val imageRes: Int,
    val title: String,
    val description: String
)

val cardData = listOf(
    CardItem(R.drawable.img_1, "Заголовок 1", "Описание карточки номер 1"),
    CardItem(R.drawable.img_2, "Заголовок 2", "Описание карточки номер 2"),
    CardItem(R.drawable.img_3, "Заголовок 3", "Описание карточки номер 3"),
    CardItem(R.drawable.img_4, "Заголовок 4", "Описание карточки номер 4"),
    CardItem(R.drawable.img_5, "Заголовок 5", "Описание карточки номер 5"),
)

@Composable
fun CardList() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        items(items = cardData) { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = item.imageRes),
                        contentDescription = item.title,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = item.description,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

// PREVIEW
@Preview(showBackground = true)
@Composable
fun PreviewList() {
    AppTheme {
        CardList()
    }
}
