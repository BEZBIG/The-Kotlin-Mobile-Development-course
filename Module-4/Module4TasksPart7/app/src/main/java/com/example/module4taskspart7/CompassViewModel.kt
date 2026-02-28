package com.example.module4taskspart7

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel для компаса.
 * Управляет сенсорами и хранит текущий азимут в StateFlow.
 * StateFlow гарантирует что значение не потеряется при пересоздании UI.
 */
class CompassViewModel : ViewModel() {

    // Текущий азимут в градусах (0-360)
    private val _azimuth = MutableStateFlow(0f)
    val azimuth: StateFlow<Float> = _azimuth.asStateFlow()

    // Флаг доступности сенсоров
    private val _sensorAvailable = MutableStateFlow(true)
    val sensorAvailable: StateFlow<Boolean> = _sensorAvailable.asStateFlow()

    // Буферы для хранения сырых данных сенсоров
    // Акселерометр даёт направление силы тяжести (ориентация телефона)
    private val accelerometerValues = FloatArray(3)
    // Магнитометр даёт направление магнитного поля Земли
    private val magnetometerValues = FloatArray(3)

    // Флаги: пришли ли данные от каждого сенсора
    private var hasAccelerometer = false
    private var hasMagnetometer = false

    // SensorManager и сами сенсоры
    private var sensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null
    private var magnetometer: Sensor? = null

    // Матрицы для вычисления азимута через SensorManager.getRotationMatrix
    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    /**
     * Слушатель событий сенсора.
     * onSensorChanged вызывается при каждом новом измерении.
     */
    private val sensorListener = object : SensorEventListener {

        override fun onSensorChanged(event: SensorEvent?) {
            event ?: return

            when (event.sensor.type) {
                // Сохраняем данные акселерометра
                Sensor.TYPE_ACCELEROMETER -> {
                    System.arraycopy(event.values, 0, accelerometerValues, 0, 3)
                    hasAccelerometer = true
                }
                // Сохраняем данные магнитометра
                Sensor.TYPE_MAGNETIC_FIELD -> {
                    System.arraycopy(event.values, 0, magnetometerValues, 0, 3)
                    hasMagnetometer = true
                }
            }

            // Вычисляем азимут только когда есть данные от обоих сенсоров
            if (hasAccelerometer && hasMagnetometer) {
                computeAzimuth()
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            // Не используем — для компаса точность меняется редко
        }
    }

    /**
     * Вычисляет азимут из данных акселерометра и магнитометра.
     * Использует стандартный метод Android: getRotationMatrix + getOrientation.
     */
    private fun computeAzimuth() {
        // getRotationMatrix вычисляет матрицу вращения из двух сенсоров
        val success = SensorManager.getRotationMatrix(
            rotationMatrix,
            null,
            accelerometerValues,
            magnetometerValues
        )

        if (success) {
            // getOrientation возвращает углы Эйлера: [азимут, тангаж, крен]
            SensorManager.getOrientation(rotationMatrix, orientationAngles)

            // orientationAngles[0] — азимут в радианах (-π до π)
            // Переводим в градусы и нормализуем до 0-360
            val azimuthDegrees = Math.toDegrees(orientationAngles[0].toDouble()).toFloat()
            val normalizedAzimuth = (azimuthDegrees + 360f) % 360f

            _azimuth.value = normalizedAzimuth
        }
    }

    /**
     * Инициализирует SensorManager и проверяет доступность сенсоров.
     * Вызывается из Activity при создании.
     */
    fun initSensors(context: Context) {
        sensorManager = context.getSystemService(SensorManager::class.java)

        accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magnetometer = sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        // Компас требует оба сенсора — если хоть один отсутствует, показываем ошибку
        _sensorAvailable.value = accelerometer != null && magnetometer != null
    }

    /**
     * Регистрирует слушатели сенсоров.
     * Вызывается в onResume — сенсоры работают только когда экран виден.
     */
    fun registerSensors() {
        if (!_sensorAvailable.value) return

        // SENSOR_DELAY_UI — достаточная частота для компаса, экономит батарею
        sensorManager?.registerListener(
            sensorListener,
            accelerometer,
            SensorManager.SENSOR_DELAY_UI
        )
        sensorManager?.registerListener(
            sensorListener,
            magnetometer,
            SensorManager.SENSOR_DELAY_UI
        )
    }

    /**
     * Отменяет регистрацию слушателей.
     * Вызывается в onPause — обязательно для экономии батареи.
     */
    fun unregisterSensors() {
        sensorManager?.unregisterListener(sensorListener)
    }

    override fun onCleared() {
        super.onCleared()
        // Дополнительная защита: отменяем сенсоры при уничтожении ViewModel
        unregisterSensors()
    }
}