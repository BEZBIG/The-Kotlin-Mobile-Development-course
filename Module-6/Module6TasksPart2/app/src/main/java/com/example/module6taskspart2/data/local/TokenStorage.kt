package com.example.module6taskspart2.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore2 by preferencesDataStore(name = "auth_prefs_part2")

class TokenStorage(private val context: Context) {

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("auth_token")
    }

    val tokenFlow: Flow<String?> = context.dataStore2.data.map { it[TOKEN_KEY] }

    suspend fun saveToken(token: String) {
        context.dataStore2.edit { it[TOKEN_KEY] = token }
    }

    suspend fun clearToken() {
        context.dataStore2.edit { it.remove(TOKEN_KEY) }
    }
}