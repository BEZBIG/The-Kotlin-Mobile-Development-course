package com.example.module5taskspart3.presentation.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.module5taskspart3.domain.model.TodoItem

/**
 * Карточка задачи — расширенная версия TodoItemCard из Модуля 3.
 * Добавлено: цвет фона из DataStore, кнопка удаления, кнопка редактирования.
 */
@Composable
fun TodoItemCard(
    todo: TodoItem,
    completedColor: String,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    // Цвет фона выполненной задачи берётся из DataStore
    val bgColor = if (todo.isCompleted) {
        when (completedColor) {
            "green"  -> Color(0xFFE8F5E9)
            "blue"   -> Color(0xFFE3F2FD)
            "yellow" -> Color(0xFFFFFDE7)
            "red"    -> Color(0xFFFFEBEE)
            else     -> Color(0xFFE8F5E9)
        }
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = todo.isCompleted, onCheckedChange = { onToggle() })

        Spacer(Modifier.width(4.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = todo.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                textDecoration = if (todo.isCompleted)
                    TextDecoration.LineThrough else TextDecoration.None,
                color = if (todo.isCompleted)
                    MaterialTheme.colorScheme.onSurfaceVariant
                else MaterialTheme.colorScheme.onSurface
            )
            if (todo.description.isNotBlank()) {
                Text(
                    text = todo.description,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textDecoration = if (todo.isCompleted)
                        TextDecoration.LineThrough else TextDecoration.None
                )
            }
        }

        IconButton(onClick = onEdit) {
            Icon(Icons.Default.Edit, contentDescription = "Редактировать",
                tint = MaterialTheme.colorScheme.primary)
        }
        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, contentDescription = "Удалить",
                tint = Color(0xFFE53935))
        }
    }
}