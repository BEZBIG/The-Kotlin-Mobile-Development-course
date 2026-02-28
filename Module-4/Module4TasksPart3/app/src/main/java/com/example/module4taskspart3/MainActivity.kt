package com.example.module4taskspart3

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.module4taskspart3.ui.theme.Module4TasksPart3Theme
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Module4TasksPart3Theme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current

    // Запрашиваем разрешение на уведомления
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    DisposableEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        onDispose { }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Прогноз погоды") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Задание 8 — цепочка обработки фото
            Task8Section(context = context)

            HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp))

            // Задание 9 — параллельный прогноз погоды
            Task9WeatherSection(context = context)
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Задание 8
// ─────────────────────────────────────────────────────────────

@Composable
fun Task8Section(context: Context) {
    val workManager = WorkManager.getInstance(context)

    val uploadInfo by workManager
        .getWorkInfosByTagLiveData("upload_tag").observeAsState()
    val compressInfo by workManager
        .getWorkInfosByTagLiveData("compress_tag").observeAsState()
    val watermarkInfo by workManager
        .getWorkInfosByTagLiveData("watermark_tag").observeAsState()

    val compressState = compressInfo?.firstOrNull()?.state
    val watermarkState = watermarkInfo?.firstOrNull()?.state
    val uploadState = uploadInfo?.firstOrNull()

    val statusText = when {
        compressState == WorkInfo.State.RUNNING -> "Сжимаем фото..."
        watermarkState == WorkInfo.State.RUNNING -> "Добавляем водяной знак..."
        uploadState?.state == WorkInfo.State.RUNNING -> "Загружаем в облако..."
        uploadState?.state == WorkInfo.State.SUCCEEDED -> {
            val path = uploadState.outputData.getString(KEY_UPLOAD_RESULT) ?: ""
            "Готово! Фото загружено\n$path"
        }
        uploadState?.state == WorkInfo.State.FAILED ||
                compressState == WorkInfo.State.FAILED -> "Ошибка! Цепочка прервана."
        else -> "Нажмите кнопку для обработки фото"
    }

    val isRunning = listOf(compressState, watermarkState, uploadState?.state).any {
        it == WorkInfo.State.RUNNING || it == WorkInfo.State.ENQUEUED
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "Задание 8: Последовательная цепочка",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(8.dp))
        Text(
            statusText,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = when (uploadState?.state) {
                WorkInfo.State.SUCCEEDED -> Color(0xFF43A047)
                WorkInfo.State.FAILED -> Color(0xFFE53935)
                else -> MaterialTheme.colorScheme.onSurface
            }
        )
        if (isRunning) {
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(Modifier.fillMaxWidth())
        }
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = { startPhotoChain(workManager) },
            enabled = !isRunning
        ) {
            Text("Начать обработку и загрузку")
        }
    }
}

private fun startPhotoChain(workManager: WorkManager) {
    val compress = OneTimeWorkRequestBuilder<CompressPhotoWorker>()
        .addTag("compress_tag").build()
    val watermark = OneTimeWorkRequestBuilder<WatermarkWorker>()
        .addTag("watermark_tag").build()
    val upload = OneTimeWorkRequestBuilder<UploadWorker>()
        .addTag("upload_tag").build()
    workManager.beginWith(compress).then(watermark).then(upload).enqueue()
}

// ─────────────────────────────────────────────────────────────
// Задание 9
// ─────────────────────────────────────────────────────────────

// Города для параллельной загрузки
private val CITIES = listOf("Москва", "Лондон", "Нью-Йорк")

@Composable
fun Task9WeatherSection(context: Context) {
    val workManager = WorkManager.getInstance(context)

    val weatherInfoList by workManager
        .getWorkInfosByTagLiveData("weather_tag").observeAsState(emptyList())
    val reportInfoList by workManager
        .getWorkInfosByTagLiveData("report_tag").observeAsState(emptyList())

    val reportInfo = reportInfoList.firstOrNull()

    val totalCities = CITIES.size
    val completedCount = weatherInfoList.count { it.state == WorkInfo.State.SUCCEEDED }
    val isAnyRunning = weatherInfoList.any {
        it.state == WorkInfo.State.RUNNING || it.state == WorkInfo.State.ENQUEUED
    }
    val isReportRunning = reportInfo?.state == WorkInfo.State.RUNNING ||
            reportInfo?.state == WorkInfo.State.ENQUEUED
    val isAllDone = reportInfo?.state == WorkInfo.State.SUCCEEDED
    val isInProgress = isAnyRunning || isReportRunning

    val statusText = when {
        isAllDone -> "Все данные получены!"
        isReportRunning -> "Все данные получены, формируем отчёт..."
        isAnyRunning && completedCount == 0 -> "Загрузка... ($totalCities в процессе)"
        isAnyRunning -> "Загрузка... ($completedCount из $totalCities готово)"
        else -> "Готов начать"
    }

    val cityStatusMap = buildCityStatusMap(weatherInfoList, CITIES)

    val reportText = if (isAllDone) {
        buildReportText(reportInfo, weatherInfoList, CITIES)
    } else null

    // Добавляем прокрутку чтобы кнопки были всегда видны
    val scrollState = androidx.compose.foundation.rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState), // вся колонка прокручивается
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = statusText,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = if (isAllDone) Color(0xFF1976D2) else MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(16.dp))

        CITIES.forEach { city ->
            val cityStatus = cityStatusMap[city]
            CityWeatherCard(
                cityName = city,
                status = cityStatus,
                isRunning = isInProgress && cityStatus == null
            )
            Spacer(Modifier.height(8.dp))
        }

        if (reportText != null) {
            Spacer(Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text(
                    text = reportText,
                    modifier = Modifier.padding(16.dp),
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Кнопки всегда внизу, но теперь видны благодаря прокрутке
        if (isInProgress) {
            Button(
                onClick = { },
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text("В процессе...")
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = {
                    workManager.cancelAllWorkByTag("weather_tag")
                    workManager.cancelAllWorkByTag("report_tag")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Отменить")
            }
        } else {
            Button(
                onClick = { startWeatherParallel(workManager) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1976D2)
                )
            ) {
                Text("Собрать прогноз")
            }
        }

        // Отступ снизу чтобы последняя кнопка не прилипала к краю
        Spacer(Modifier.height(16.dp))
    }
}

/**
 * Карточка одного города с его статусом и температурой.
 */
@Composable
fun CityWeatherCard(
    cityName: String,
    status: CityWeatherStatus?,
    isRunning: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = cityName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = when {
                        status != null -> "Готово"
                        isRunning -> "Загружается..."
                        else -> "Ожидание"
                    },
                    fontSize = 13.sp,
                    color = when {
                        status != null -> Color(0xFF1976D2)
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }

            when {
                // Загружено — показываем температуру
                status != null -> {
                    Text(
                        text = "${status.temperature}°C",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                // Идёт загрузка — спиннер
                isRunning -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Вспомогательные классы и функции
// ─────────────────────────────────────────────────────────────

/**
 * Данные о погоде одного города после загрузки.
 */
data class CityWeatherStatus(
    val temperature: Int,
    val condition: String
)

/**
 * Строим карту город → статус из списка WorkInfo.
 */
private fun buildCityStatusMap(
    workInfoList: List<WorkInfo>,
    cities: List<String>
): Map<String, CityWeatherStatus> {
    val result = mutableMapOf<String, CityWeatherStatus>()
    workInfoList.forEachIndexed { index, workInfo ->
        if (workInfo.state == WorkInfo.State.SUCCEEDED) {
            val cityName = cities.getOrNull(index) ?: return@forEachIndexed
            val temp = workInfo.outputData.getInt(KEY_TEMPERATURE, 0)
            val condition = workInfo.outputData.getString(KEY_WEATHER_CONDITION) ?: "ясно"
            result[cityName] = CityWeatherStatus(temp, condition)
        }
    }
    return result
}

/**
 * Строит текст итогового отчёта из данных Worker-ов.
 */
private fun buildReportText(
    reportInfo: WorkInfo?,
    weatherInfoList: List<WorkInfo>,
    cities: List<String>
): String {
    val cityLines = weatherInfoList.mapIndexed { index, workInfo ->
        val city = cities.getOrNull(index) ?: ""
        val temp = workInfo.outputData.getInt(KEY_TEMPERATURE, 0)
        val condition = workInfo.outputData.getString(KEY_WEATHER_CONDITION) ?: "ясно"
        "$city: ${temp}°C, $condition"
    }.joinToString("\n")

    val avgTemp = reportInfo?.outputData?.getInt(KEY_TEMPERATURE, 0) ?: 0

    return "Итоговый прогноз:\n$cityLines\n\nСредняя температура: ${avgTemp}°C"
}

/**
 * Запускает параллельную загрузку погоды для всех городов.
 */
private fun startWeatherParallel(workManager: WorkManager) {
    val weatherRequests = CITIES.map { city ->
        OneTimeWorkRequestBuilder<WeatherWorker>()
            .setInputData(workDataOf(KEY_CITY_NAME to city))
            .addTag("weather_tag")
            .build()
    }

    // Вычисляем среднюю температуру заранее (заглушка для передачи в ReportWorker)
    val avgTemp = (-5..20).random()

    val reportRequest = OneTimeWorkRequestBuilder<WeatherReportWorker>()
        .setInputData(workDataOf(KEY_TEMPERATURE to avgTemp))
        .addTag("report_tag")
        .build()

    // Все weatherRequests запускаются параллельно, затем reportRequest
    workManager
        .beginWith(weatherRequests)
        .then(reportRequest)
        .enqueue()
}