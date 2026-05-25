package com.example.module6taskspart7.ble

// Парсит сырые байты из характеристики Heart Rate Measurement
object HeartRateParser {

    fun parse(bytes: ByteArray): Int {
        if (bytes.isEmpty()) return -1

        // Первый байт — флаги
        val flags = bytes[0].toInt()

        // Бит 0 флагов определяет формат значения пульса
        // 0 = UINT8 (1 байт), 1 = UINT16 (2 байта)
        return if (flags and 0x01 == 0) {
            // Пульс в одном байте
            bytes[1].toInt() and 0xFF
        } else {
            // Пульс в двух байтах (little-endian)
            (bytes[1].toInt() and 0xFF) or ((bytes[2].toInt() and 0xFF) shl 8)
        }
    }
}