package com.example.textbook.core.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    object Keys {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val DYNAMIC_COLORS = booleanPreferencesKey("dynamic_colors")
        val FONT_SIZE = intPreferencesKey("font_size")
        val WORD_WRAP = booleanPreferencesKey("word_wrap")
        val AUTO_SAVE = booleanPreferencesKey("auto_save")
        val SYNTAX_HIGHLIGHT = booleanPreferencesKey("syntax_highlight")
    }

    val darkMode: Flow<Boolean> = dataStore.data.map { it[Keys.DARK_MODE] ?: false }
    val dynamicColors: Flow<Boolean> = dataStore.data.map { it[Keys.DYNAMIC_COLORS] ?: true }
    val fontSize: Flow<Int> = dataStore.data.map { it[Keys.FONT_SIZE] ?: 14 }
    val wordWrap: Flow<Boolean> = dataStore.data.map { it[Keys.WORD_WRAP] ?: true }
    val autoSave: Flow<Boolean> = dataStore.data.map { it[Keys.AUTO_SAVE] ?: true }

    suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { it[Keys.DARK_MODE] = enabled }
    }

    suspend fun setFontSize(size: Int) {
        dataStore.edit { it[Keys.FONT_SIZE] = size }
    }
    
    suspend fun setWordWrap(enabled: Boolean) {
        dataStore.edit { it[Keys.WORD_WRAP] = enabled }
    }
}
