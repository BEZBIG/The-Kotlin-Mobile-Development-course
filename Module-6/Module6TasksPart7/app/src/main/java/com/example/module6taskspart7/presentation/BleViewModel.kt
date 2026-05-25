package com.example.module6taskspart7.presentation

import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.BluetoothDevice
import androidx.lifecycle.AndroidViewModel
import com.example.module6taskspart7.ble.BleManager
import kotlinx.coroutines.flow.StateFlow

@SuppressLint("MissingPermission")
class BleViewModel(application: Application) : AndroidViewModel(application) {

    private val bleManager = BleManager(application)

    // Прокидываем состояния из BleManager в UI
    val scannedDevices: StateFlow<List<BluetoothDevice>> = bleManager.scannedDevices
    val connectionStatus: StateFlow<String> = bleManager.connectionStatus
    val heartRate: StateFlow<Int?> = bleManager.heartRate

    // Запускаем сканирование
    fun startScan() = bleManager.startScan()

    // Подключаемся к устройству
    fun connect(device: BluetoothDevice) = bleManager.connect(device)

    // Отключаемся
    fun disconnect() = bleManager.disconnect()

    // Останавливаем BLE при уничтожении ViewModel
    override fun onCleared() {
        super.onCleared()
        bleManager.disconnect()
    }
}