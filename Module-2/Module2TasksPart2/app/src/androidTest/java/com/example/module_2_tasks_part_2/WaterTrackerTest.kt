package com.example.module_2_tasks_part_2

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class WaterTrackerTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testAddWaterButton() {
        // 1) Проверить наличие и корректную работу кнопки добавления жидкости
        composeTestRule.setContent {
            MaterialTheme {
                WaterTracker()
            }
        }

        // Проверяем начальное состояние
        composeTestRule.onNodeWithText("100 мл").assertExists()

        // Нажимаем кнопку добавления воды 1 раз
        composeTestRule.onNodeWithText("+250 мл").performClick()

        // Проверяем, что количество воды увеличилось до 350
        composeTestRule.onNodeWithText("350 мл").assertExists()
    }

    @Test
    fun testCompleteDayButton() {
        // 2) Проверить наличие и корректную работу кнопки "Завершить день"
        composeTestRule.setContent {
            MaterialTheme {
                WaterTracker()
            }
        }

        // Добавляем достаточно воды для достижения цели (100 + 250*6 = 1600 мл)
        repeat(6) {
            composeTestRule.onNodeWithText("+250 мл").performClick()
        }

        // Проверяем, что достигли 1600 мл
        composeTestRule.onNodeWithText("1600 мл").assertExists()

        // Нажимаем кнопку завершения дня
        composeTestRule.onNodeWithText("Завершить день").performClick()

        // Проверяем, что счетчик воды сбросился
        composeTestRule.onNodeWithText("0 мл").assertExists()

        // Проверяем, что счетчик дней увеличился
        composeTestRule.onNodeWithText("Дней подряд: 1").assertExists()
    }

    @Test
    fun testStreakCounterDisplay() {
        // 3) Проверить, что выводится необходимый текст со значением количества дней
        composeTestRule.setContent {
            MaterialTheme {
                WaterTracker()
            }
        }

        // Проверяем начальное отображение счетчика дней
        composeTestRule.onNodeWithText("Дней подряд: 0").assertExists()

        // Симулируем 1 успешный день
        repeat(6) {
            composeTestRule.onNodeWithText("+250 мл").performClick()
        }
        composeTestRule.onNodeWithText("Завершить день").performClick()

        // Проверяем, что счетчик дней увеличился до 1
        composeTestRule.onNodeWithText("Дней подряд: 1").assertExists()
    }

    @Test
    fun testStreakReset() {
        // Дополнительный тест: проверка сброса счетчика при недостаточном количестве воды
        composeTestRule.setContent {
            MaterialTheme {
                WaterTracker()
            }
        }

        // Сначала создаем серию из 1 дня
        repeat(6) {
            composeTestRule.onNodeWithText("+250 мл").performClick()
        }
        composeTestRule.onNodeWithText("Завершить день").performClick()

        // Проверяем, что счетчик дней = 1
        composeTestRule.onNodeWithText("Дней подряд: 1").assertExists()

        // Теперь добавляем недостаточно воды (только 500 мл = 2 нажатия)
        repeat(2) {
            composeTestRule.onNodeWithText("+250 мл").performClick()
        }
        composeTestRule.onNodeWithText("Завершить день").performClick()

        // Проверяем, что счетчик сбросился
        composeTestRule.onNodeWithText("Дней подряд: 0").assertExists()
    }

    @Test
    fun testAllButtonsExist() {
        // Простой тест для проверки наличия всех элементов
        composeTestRule.setContent {
            MaterialTheme {
                WaterTracker()
            }
        }

        composeTestRule.onNodeWithText("Трекер воды").assertExists()
        composeTestRule.onNodeWithText("100 мл").assertExists()
        composeTestRule.onNodeWithText("+250 мл").assertExists()
        composeTestRule.onNodeWithText("Завершить день").assertExists()
        composeTestRule.onNodeWithText("Дней подряд: 0").assertExists()
        composeTestRule.onNodeWithText("Цель: 1500 мл в день").assertExists()
    }
}