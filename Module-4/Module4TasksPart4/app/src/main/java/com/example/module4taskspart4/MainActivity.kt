package com.example.module4taskspart4

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.module4taskspart4.ui.theme.Module4TasksPart4Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Module4TasksPart4Theme {
                LocationScreen()
            }
        }
    }
}

/**
 * Состояние экрана геолокации.
 * Sealed class удобно описывает все возможные состояния UI.
 */
sealed class LocationState {
    // Начальное состояние — ничего не запрашивалось
    object Idle : LocationState()

    // Идёт загрузка координат и адреса
    object Loading : LocationState()

    // Успешно получены координаты и адрес
    data class Success(
        val latitude: Double,
        val longitude: Double,
        val address: String
    ) : LocationState()

    // Произошла ошибка с описанием
    data class Error(val message: String) : LocationState()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val locationHelper = remember { LocationHelper(context) }

    // Текущее состояние экрана
    var locationState by remember { mutableStateOf<LocationState>(LocationState.Idle) }

    // Лончер для запроса сразу двух разрешений (FINE + COARSE)
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (fineGranted || coarseGranted) {
            // Разрешение получено — запускаем получение локации
            scope.launch {
                fetchLocation(locationHelper) { state ->
                    locationState = state
                }
            }
        } else {
            // Пользователь отказал в разрешении
            locationState = LocationState.Error(
                "Разрешение на геолокацию отклонено.\nПожалуйста, разрешите доступ в настройках."
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Моё местоположение") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // Иконка геолокации (системная)
            Text(
                text = "📍",
                fontSize = 64.sp
            )

            Spacer(Modifier.height(24.dp))

            // Основной контент зависит от состояния
            when (val state = locationState) {

                // Начальное состояние — подсказка
                is LocationState.Idle -> {
                    Text(
                        text = "Нажмите кнопку, чтобы определить\nваше текущее местоположение",
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Загрузка — спиннер
                is LocationState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "Определяем местоположение...",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Успех — показываем адрес и координаты
                is LocationState.Success -> {
                    AddressCard(
                        address = state.address,
                        latitude = state.latitude,
                        longitude = state.longitude
                    )
                }

                // Ошибка — показываем сообщение
                is LocationState.Error -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFEBEE)
                        )
                    ) {
                        Text(
                            text = state.message,
                            modifier = Modifier.padding(16.dp),
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center,
                            color = Color(0xFFE53935)
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // Кнопка недоступна во время загрузки
            Button(
                onClick = {
                    val hasFine = ContextCompat.checkSelfPermission(
                        context, Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED

                    val hasCoarse = ContextCompat.checkSelfPermission(
                        context, Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED

                    if (hasFine || hasCoarse) {
                        // Разрешения уже есть — сразу получаем локацию
                        scope.launch {
                            fetchLocation(locationHelper) { state ->
                                locationState = state
                            }
                        }
                    } else {
                        // Запрашиваем оба разрешения сразу
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                },
                enabled = locationState !is LocationState.Loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (locationState is LocationState.Success)
                        "Обновить местоположение"
                    else
                        "Получить мой адрес",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

/**
 * Карточка с адресом и координатами.
 */
@Composable
fun AddressCard(address: String, latitude: Double, longitude: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            // Заголовок карточки
            Text(
                text = "Ваш адрес",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(8.dp))

            // Адрес крупным текстом
            Text(
                text = address,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 26.sp
            )

            Spacer(Modifier.height(16.dp))

            HorizontalDivider()

            Spacer(Modifier.height(16.dp))

            // Координаты
            Text(
                text = "Координаты",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(8.dp))

            // Широта
            CoordinateRow(label = "Широта (lat)", value = "%.6f".format(latitude))
            Spacer(Modifier.height(4.dp))
            // Долгота
            CoordinateRow(label = "Долгота (lng)", value = "%.6f".format(longitude))
        }
    }
}

/**
 * Строка с меткой и значением координаты.
 */
@Composable
fun CoordinateRow(label: String, value: String) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Получает текущее местоположение и адрес.
 * Вынесено в отдельную функцию чтобы не дублировать код.
 */
private suspend fun fetchLocation(
    locationHelper: LocationHelper,
    onStateChanged: (LocationState) -> Unit
) {
    // Показываем загрузку
    onStateChanged(LocationState.Loading)

    try {
        // Получаем координаты через FusedLocationProviderClient
        val location = withContext(Dispatchers.IO) {
            locationHelper.getCurrentLocation()
        }

        if (location == null) {
            onStateChanged(
                LocationState.Error(
                    "Не удалось определить местоположение.\n" +
                            "Проверьте, включён ли GPS на устройстве."
                )
            )
            return
        }

        // Получаем адрес по координатам (reverse geocoding)
        val address = withContext(Dispatchers.IO) {
            locationHelper.getAddressFromLocation(location.latitude, location.longitude)
        }

        onStateChanged(
            LocationState.Success(
                latitude = location.latitude,
                longitude = location.longitude,
                address = address
            )
        )
    } catch (e: Exception) {
        onStateChanged(
            LocationState.Error("Произошла ошибка: ${e.message}")
        )
    }
}
