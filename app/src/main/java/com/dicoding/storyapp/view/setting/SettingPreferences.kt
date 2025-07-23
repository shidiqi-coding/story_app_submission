package com.dicoding.storyapp.view.setting

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingPreferences private constructor(private val context: Context) {

    companion object {
        private val THEME_KEY = intPreferencesKey("theme_setting")

        @Volatile
        private var INSTANCE: SettingPreferences? = null

        fun getInstance(context: Context): SettingPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = SettingPreferences(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }

    fun getThemeSetting(): Flow<Int> {
        return context.dataStore.data.map { preferences ->
            preferences[THEME_KEY] ?: 0
        }
    }

    suspend fun saveThemeSetting(themeChoice: Int) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = themeChoice
        }
    }
}
