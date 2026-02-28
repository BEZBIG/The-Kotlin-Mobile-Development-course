package com.example.module4taskspart7

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.module4taskspart7.ui.theme.Module4TasksPart7Theme
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Module4TasksPart7Theme {
                CompassScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompassScreen(
    compassViewModel: CompassViewModel = viewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Инициализируем сенсоры один раз при создании composable
    remember(context) {
        compassViewModel.initSensors(context)
        true
    }

    // Подписываемся на события жизненного цикла Activity
    // чтобы регистрировать/отменять сенсоры в onResume/onPause
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                // Экран стал виден — включаем сенсоры
                Lifecycle.Event.ON_RESUME -> compassViewModel.registerSensors()
                // Экран ушёл в фон — выключаем сенсоры (экономим батарею)
                Lifecycle.Event.ON_PAUSE -> compassViewModel.unregisterSensors()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Получаем данные из ViewModel через StateFlow
    val azimuth by compassViewModel.azimuth.collectAsStateWithLifecycle()
    val sensorAvailable by compassViewModel.sensorAvailable.collectAsStateWithLifecycle()

    // Тёмный фон для стильного вида компаса
    val backgroundColor = Color(0xFF1A1A2E)
    val surfaceColor = Color(0xFF16213E)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Компас",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor
                )
            )
        },
        containerColor = backgroundColor
    ) { innerPadding ->

        if (!sensorAvailable) {
            // Ошибка: сенсоры недоступны
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Устройство не поддерживает\nдатчик ориентации",
                    fontSize = 20.sp,
                    color = Color(0xFFE53935),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(32.dp)
                )
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // Название направления по азимуту
            val directionName = getDirectionName(azimuth)
            Text(
                text = directionName,
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFE0E0E0)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Компас — занимает большую часть экрана
            CompassView(
                azimuth = azimuth,
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .aspectRatio(1f) // квадрат
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Значение азимута крупным текстом
            Text(
                text = "Азимут: ${azimuth.toInt()}°",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "0° — север · 90° — восток · 180° — юг · 270° — запад",
                fontSize = 12.sp,
                color = Color(0xFF9E9E9E),
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Composable с анимированным компасом.
 * Рисует: внешний круг, деления, буквы сторон света, стрелку.
 * Стрелка плавно поворачивается благодаря animateFloatAsState.
 */
@Composable
fun CompassView(
    azimuth: Float,
    modifier: Modifier = Modifier
) {
    // Анимируем угол поворота стрелки — плавное движение без рывков
    val animatedAzimuth by animateFloatAsState(
        targetValue = -azimuth, // минус — стрелка указывает на север, а не вращается вместе с диском
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        ),
        label = "compass_rotation"
    )

    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.width / 2f * 0.9f

        // Рисуем внешний круг компаса
        drawCompassDisk(center, radius)

        // Рисуем деления и буквы сторон света
        drawCompassMarkings(center, radius)

        // Рисуем стрелку с анимацией поворота
        rotate(degrees = animatedAzimuth, pivot = center) {
            drawCompassArrow(center, radius * 0.75f)
        }

        // Центральная точка поверх стрелки
        drawCircle(
            color = Color(0xFF37474F),
            radius = radius * 0.06f,
            center = center
        )
        drawCircle(
            color = Color(0xFFB0BEC5),
            radius = radius * 0.03f,
            center = center
        )
    }
}

/**
 * Рисует фоновый диск компаса с градиентом.
 */
private fun DrawScope.drawCompassDisk(center: Offset, radius: Float) {
    // Внешняя обводка
    drawCircle(
        color = Color(0xFF37474F),
        radius = radius + 4.dp.toPx(),
        center = center
    )

    // Основной диск с радиальным градиентом
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFF263238), Color(0xFF1A1A2E)),
            center = center,
            radius = radius
        ),
        radius = radius,
        center = center
    )
}

/**
 * Рисует деления шкалы и буквы сторон света (N, E, S, W).
 */
private fun DrawScope.drawCompassMarkings(center: Offset, radius: Float) {
    val ticksCount = 72 // деление каждые 5 градусов

    for (i in 0 until ticksCount) {
        val angleDeg = i * (360f / ticksCount)
        val angleRad = Math.toRadians(angleDeg.toDouble())

        val isMajor = i % (ticksCount / 4) == 0
        val isMedium = i % (ticksCount / 36) == 0

        val tickLength = when {
            isMajor -> radius * 0.18f
            isMedium -> radius * 0.10f
            else -> radius * 0.05f
        }

        val tickWidth = when {
            isMajor -> 3.dp.toPx()
            isMedium -> 1.5.dp.toPx()
            else -> 1.dp.toPx()
        }

        val tickColor = when {
            isMajor -> Color.White
            else -> Color(0xFF78909C)
        }

        val outerX = center.x + radius * sin(angleRad).toFloat()
        val outerY = center.y - radius * cos(angleRad).toFloat()
        val innerX = center.x + (radius - tickLength) * sin(angleRad).toFloat()
        val innerY = center.y - (radius - tickLength) * cos(angleRad).toFloat()

        drawLine(
            color = tickColor,
            start = Offset(outerX, outerY),
            end = Offset(innerX, innerY),
            strokeWidth = tickWidth
        )
    }

    // Буквы сторон света рисуем через android.graphics.Paint напрямую
    val directions = listOf("N", "E", "S", "W")
    val directionAngles = listOf(0f, 90f, 180f, 270f)
    val directionColors = listOf(
        android.graphics.Color.rgb(239, 83, 80),  // N — красный
        android.graphics.Color.WHITE,
        android.graphics.Color.WHITE,
        android.graphics.Color.WHITE
    )

    val textPaint = android.graphics.Paint().apply {
        textSize = radius * 0.14f
        isFakeBoldText = true
        textAlign = android.graphics.Paint.Align.CENTER
        isAntiAlias = true
        typeface = android.graphics.Typeface.DEFAULT_BOLD
    }

    directions.forEachIndexed { index, letter ->
        val angleRad = Math.toRadians(directionAngles[index].toDouble())
        val letterRadius = radius * 0.72f

        val x = center.x + letterRadius * sin(angleRad).toFloat()
        val y = center.y - letterRadius * cos(angleRad).toFloat()

        textPaint.color = directionColors[index]

        // Центрируем текст по вертикали
        val textOffset = (textPaint.descent() - textPaint.ascent()) / 2f - textPaint.descent()

        // drawIntoCanvas — правильный способ использовать Canvas в Compose DrawScope
        drawIntoCanvas { canvas ->
            canvas.nativeCanvas.drawText(letter, x, y + textOffset, textPaint)
        }
    }
}

/**
 * Рисует двухцветную стрелку компаса.
 * Красная часть — север, серая — юг.
 */
private fun DrawScope.drawCompassArrow(center: Offset, arrowLength: Float) {
    val arrowWidth = arrowLength * 0.12f

    // Северная половина стрелки (красная, вверх)
    val northPath = Path().apply {
        moveTo(center.x, center.y - arrowLength) // острие
        lineTo(center.x - arrowWidth, center.y)  // левый бок
        lineTo(center.x + arrowWidth, center.y)  // правый бок
        close()
    }

    // Южная половина стрелки (серая, вниз)
    val southPath = Path().apply {
        moveTo(center.x, center.y + arrowLength * 0.85f) // конец
        lineTo(center.x - arrowWidth, center.y)           // левый бок
        lineTo(center.x + arrowWidth, center.y)           // правый бок
        close()
    }

    // Тень для объёма
    drawPath(
        path = northPath,
        color = Color(0x33000000),
        alpha = 0.5f
    )

    // Красная северная половина
    drawPath(
        path = northPath,
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFFEF5350), Color(0xFFC62828)),
            startY = center.y - arrowLength,
            endY = center.y
        )
    )

    // Серая южная половина
    drawPath(
        path = southPath,
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF78909C), Color(0xFF37474F)),
            startY = center.y,
            endY = center.y + arrowLength * 0.85f
        )
    )
}

/**
 * Возвращает название направления по азимуту.
 */
private fun getDirectionName(azimuth: Float): String {
    return when {
        azimuth < 22.5f || azimuth >= 337.5f -> "Север"
        azimuth < 67.5f -> "Северо-восток"
        azimuth < 112.5f -> "Восток"
        azimuth < 157.5f -> "Юго-восток"
        azimuth < 202.5f -> "Юг"
        azimuth < 247.5f -> "Юго-запад"
        azimuth < 292.5f -> "Запад"
        else -> "Северо-запад"
    }
}
