// задание 7
package com.example.module4taskspart2

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import kotlin.random.Random

/**
 * Задание 7: Bound-сервис.
 * Каждую секунду генерирует случайное число от 0 до 100.
 * UI получает числа через колбэк-функцию.
 */
class RandomNumberService : Service() {

    // Binder для передачи клиенту ссылки на сервис
    private val binder = RandomNumberBinder()

    // Колбэк, который вызывается при каждом новом числе
    var onNewNumber: ((Int) -> Unit)? = null

    // Последнее сгенерированное число
    var lastNumber: Int = 0
        private set

    private var generatorThread: Thread? = null

    @Volatile
    private var isRunning = false

    /**
     * Внутренний класс Binder: даёт клиенту доступ к сервису.
     */
    inner class RandomNumberBinder : Binder() {
        fun getService(): RandomNumberService = this@RandomNumberService
    }

    override fun onBind(intent: Intent): IBinder {
        // Запускаем генерацию при привязке клиента
        startGenerating()
        return binder
    }

    override fun onUnbind(intent: Intent): Boolean {
        // Останавливаем генерацию когда клиент отвязался
        stopGenerating()
        return false
    }

    /**
     * Запускает поток генерации случайных чисел.
     */
    private fun startGenerating() {
        isRunning = true
        generatorThread = Thread {
            while (isRunning) {
                lastNumber = Random.nextInt(0, 101) // Генерируем число от 0 до 100

                // Вызываем колбэк в главном потоке через Handler
                android.os.Handler(mainLooper).post {
                    onNewNumber?.invoke(lastNumber)
                }

                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    break
                }
            }
        }
        generatorThread?.start()
    }

    /**
     * Останавливает поток генерации.
     */
    private fun stopGenerating() {
        isRunning = false
        generatorThread?.interrupt()
        onNewNumber = null
    }

    override fun onDestroy() {
        super.onDestroy()
        stopGenerating()
    }
}