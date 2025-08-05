package com.dicoding.storyapp.view.helper

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import java.util.Locale

object LocaleHelper {
//    private const val LANGUAGE_PREFERENCE = "settings"
//    private const val LANGUAGE_KEY = "language_setting"
//    private const val DEFAULT_LANGUAGE = "system"

    fun onAttach(context: Context): Context {
        return applyLanguage(context, getSavedLanguage(context))
    }

    fun getSavedLanguage(context: Context?): String {
        val prefs = context?.getSharedPreferences("language_prefs", Context.MODE_PRIVATE)
        return prefs?.getString("lang_code", "en") ?: "en"
    }

    fun saveLanguage(context: Context, lang: String?) {
        val prefs = context.getSharedPreferences("language_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("lang_code", lang ?: "en").apply()
    }

    fun applyLanguage(context: Context, language: String?): Context {
        if (language.isNullOrEmpty()) {
            return updateResources(context, Locale.getDefault())
        }

        val locale = Locale(language)
        Locale.setDefault(locale)
        return updateResources(context, locale)
    }

    fun wrap(context: Context, language: String): Context {
        return applyLanguage(context, language)
    }

    @Suppress("DEPRECATION")
    private fun updateResources(context: Context, locale: Locale): Context {
        val config = Configuration(context.resources.configuration)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale)
            config.setLayoutDirection(locale)
            return context.createConfigurationContext(config)
        } else {
            config.locale = locale
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
            return context
        }
    }
}
