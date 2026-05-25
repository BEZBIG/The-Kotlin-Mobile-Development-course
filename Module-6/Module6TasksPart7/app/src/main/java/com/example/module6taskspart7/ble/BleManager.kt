package com.example.module6taskspart7.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Build
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

// UUID стандартного Heart Rate сервиса
val HEART_RATE_SERVICE_UUID: UUID = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb")

// UUID характеристики с данными пульса
val HEART_RATE_MEASUREMENT_UUID: UUID = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb")

// UUID дескриптора для включения уведомлений
val CLIENT_CHARACTERISTIC_CONFIG_UUID: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

@SuppressLint("MissingPermission")
class BleManager(private val context: Context) {

    private val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter = bluetoothManager.adapter
    private val scanner = bluetoothAdapter.bluetoothLeScanner

    private var gatt: BluetoothGatt? = null

    // Состояния которые слушает ViewModel
    private val _scannedDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val scannedDevices: StateFlow<List<BluetoothDevice>> = _scannedDevices

    private val _connectionStatus = MutableStateFlow("Отключено")
    val connectionStatus: StateFlow<String> = _connectionStatus

    private val _heartRate = MutableStateFlow<Int?>(null)
    val heartRate: StateFlow<Int?> = _heartRate

    // Коллбэк сканирования — добавляем найденные устройства в список
    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val device = result.device
            val current = _scannedDevices.value
            if (current.none { it.address == device.address }) {
                _scannedDevices.value = current + device
            }
        }
    }

    // Запускаем сканирование BLE устройств
    fun startScan() {
        _scannedDevices.value = emptyList()
        _heartRate.value = null
        scanner.startScan(scanCallback)
    }

    // Останавливаем сканирование
    fun stopScan() {
        scanner.stopScan(scanCallback)
    }

    // Подключаемся к выбранному устройству
    fun connect(device: BluetoothDevice) {
        stopScan()
        _connectionStatus.value = "Подключение..."
        gatt = device.connectGatt(context, false, gattCallback)
    }

    // Отключаемся от устройства
    fun disconnect() {
        gatt?.disconnect()
        gatt?.close()
        gatt = null
        _connectionStatus.value = "Отключено"
        _heartRate.value = null
    }

    // Коллбэк GATT — обрабатываем события подключения и данных
    private val gattCallback = object : BluetoothGattCallback() {

        // Статус подключения изменился
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            when (newState) {
                BluetoothGatt.STATE_CONNECTED -> {
                    _connectionStatus.value = "Подключено"
                    // Обнаруживаем сервисы устройства
                    gatt.discoverServices()
                }
                BluetoothGatt.STATE_DISCONNECTED -> {
                    _connectionStatus.value = "Отключено"
                    _heartRate.value = null
                }
            }
        }

        // Сервисы устройства обнаружены — ищем Heart Rate
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            val service = gatt.getService(HEART_RATE_SERVICE_UUID) ?: return
            val characteristic = service.getCharacteristic(HEART_RATE_MEASUREMENT_UUID) ?: return

            // Включаем уведомления локально
            gatt.setCharacteristicNotification(characteristic, true)

            // Включаем уведомления на самом устройстве через дескриптор
            val descriptor = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_UUID)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                gatt.writeDescriptor(descriptor, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
            } else {
                @Suppress("DEPRECATION")
                descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                @Suppress("DEPRECATION")
                gatt.writeDescriptor(descriptor)
            }
        }

        // Пришло уведомление с новым значением пульса
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            if (characteristic.uuid == HEART_RATE_MEASUREMENT_UUID) {
                val bpm = HeartRateParser.parse(value)
                if (bpm > 0) _heartRate.value = bpm
            }
        }

        // Для старых версий Android (до API 33)
        @Suppress("DEPRECATION")
        @Deprecated("Deprecated in API 33")
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            if (characteristic.uuid == HEART_RATE_MEASUREMENT_UUID) {
                val value = characteristic.value ?: return
                val bpm = HeartRateParser.parse(value)
                if (bpm > 0) _heartRate.value = bpm
            }
        }
    }
}