package com.dicoding.storyapp.view.setting

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingDataStore by preferencesDataStore(name = "settings")

class SettingPreferences private constructor(private val context: Context) {

    private val THEME_KEY = intPreferencesKey("theme_setting")
    private val LANGUAGE_KEY = stringPreferencesKey("language_setting")
    private val TOKEN_KEY = stringPreferencesKey("auth_token")



    fun getToken(): Flow<String?> {
        return context.settingDataStore.data.map { preferences ->
            preferences[TOKEN_KEY]
        }
    }

    suspend fun saveToken(token: String) {
        context.settingDataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    fun getThemeSetting(): Flow<Int> {
        return context.settingDataStore.data.map { preferences ->
            preferences[THEME_KEY] ?: 0
        }
    }

    suspend fun saveThemeSetting(theme: Int) {
        context.settingDataStore.edit { preferences ->
            preferences[THEME_KEY] = theme
        }
    }

    fun getLanguageSetting(): Flow<String?> {
        return context.settingDataStore.data.map { preferences ->
            preferences[LANGUAGE_KEY]
        }
    }

    suspend fun saveLanguageSetting(language: String?) {
        context.settingDataStore.edit { preferences ->
            if (language == null) {
                preferences.remove(LANGUAGE_KEY)
            } else {
                preferences[LANGUAGE_KEY] = language
            }
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: SettingPreferences? = null

        fun getInstance(context: Context): SettingPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = SettingPreferences(context)
                INSTANCE = instance
                instance
            }
        }
    }
}
