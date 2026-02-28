package com.example.module4taskspart4

import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Build
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume

/**
 * Вспомогательный класс для работы с геолокацией.
 * Инкапсулирует логику получения координат и адреса,
 * чтобы не перегружать MainActivity.
 */
class LocationHelper(private val context: Context) {

    // FusedLocationProviderClient — основной API для получения координат
    private val fusedClient = LocationServices.getFusedLocationProviderClient(context)

    /**
     * Получает текущее местоположение устройства.
     * Использует getCurrentLocation() — рекомендуемый способ для одноразового запроса.
     * Возвращает Location или null если не удалось получить.
     */
    @Suppress("MissingPermission") // разрешения проверяются в UI перед вызовом
    suspend fun getCurrentLocation(): Location? {
        return suspendCancellableCoroutine { continuation ->
            val cancellationToken = CancellationTokenSource()

            // Отменяем запрос если корутина была отменена
            continuation.invokeOnCancellation {
                cancellationToken.cancel()
            }

            fusedClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY, // GPS + Wi-Fi + вышки
                cancellationToken.token
            ).addOnSuccessListener { location ->
                continuation.resume(location)
            }.addOnFailureListener {
                continuation.resume(null)
            }
        }
    }

    /**
     * Выполняет обратное геокодирование: координаты → адрес.
     * Поддерживает как новый асинхронный API (Android 13+),
     * так и синхронный запасной вариант для старых версий.
     */
    suspend fun getAddressFromLocation(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(context, Locale.getDefault())

        return suspendCancellableCoroutine { continuation ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Android 13+: асинхронный метод с GeocodeListener (не блокирует поток)
                geocoder.getFromLocation(latitude, longitude, 1,
                    object : android.location.Geocoder.GeocodeListener {
                        override fun onGeocode(addresses: MutableList<android.location.Address>) {
                            val address = addresses.firstOrNull()
                            if (address != null) {
                                continuation.resume(formatAddress(address))
                            } else {
                                continuation.resume("Адрес не найден")
                            }
                        }

                        override fun onError(errorMessage: String?) {
                            continuation.resume("Ошибка геокодирования: $errorMessage")
                        }
                    }
                )
            } else {
                // Android 12 и ниже: синхронный метод (вызываем в IO-потоке через корутину)
                try {
                    @Suppress("DEPRECATION")
                    val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                    val address = addresses?.firstOrNull()
                    if (address != null) {
                        continuation.resume(formatAddress(address))
                    } else {
                        continuation.resume("Адрес не найден")
                    }
                } catch (e: Exception) {
                    continuation.resume("Ошибка: ${e.message}")
                }
            }
        }
    }

    /**
     * Форматирует объект Address в читаемую строку.
     * Собирает: улица, город, регион, страна.
     */
    private fun formatAddress(address: android.location.Address): String {
        val parts = mutableListOf<String>()

        // Улица и номер дома
        val street = address.thoroughfare
        val houseNumber = address.subThoroughfare
        if (street != null) {
            parts.add(if (houseNumber != null) "$street, $houseNumber" else street)
        }

        // Город или населённый пункт
        val city = address.locality ?: address.subAdminArea
        if (city != null) parts.add(city)

        // Регион/область
        val region = address.adminArea
        if (region != null) parts.add(region)

        // Страна
        val country = address.countryName
        if (country != null) parts.add(country)

        return if (parts.isNotEmpty()) parts.joinToString("\n") else "Адрес не определён"
    }
}