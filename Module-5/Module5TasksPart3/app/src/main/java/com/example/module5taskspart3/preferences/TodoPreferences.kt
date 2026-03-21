package com.example.module5taskspart3.preferences

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

// Расширение создаёт единственный экземпляр DataStore на весь Context
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "todo_preferences"
)

/**
 * Репозиторий настроек через DataStore.
 * Хранит:
 * - цвет фона выполненных задач (строка: "green", "blue", "yellow", "red")
 * - флаг однократного импорта JSON
 */
class TodoPreferences(private val context: Context) {

    companion object {
        val COMPLETED_COLOR = stringPreferencesKey("completed_color")
        val JSON_IMPORTED = booleanPreferencesKey("json_imported")
    }

    val completedColor: Flow<String> = context.dataStore.data
        .catch { e ->
            if (e is IOException) {
                Log.e("TodoPreferences", "DataStore read error", e)
                emit(emptyPreferences())
            } else throw e
        }
        .map { prefs -> prefs[COMPLETED_COLOR] ?: "green" }

    val jsonImported: Flow<Boolean> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { prefs -> prefs[JSON_IMPORTED] ?: false }

    suspend fun saveCompletedColor(color: String) {
        context.dataStore.edit { prefs -> prefs[COMPLETED_COLOR] = color }
    }

    suspend fun markJsonImported() {
        context.dataStore.edit { prefs -> prefs[JSON_IMPORTED] = true }
    }
}