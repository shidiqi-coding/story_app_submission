package com.dicoding.storyapp

import android.content.Context
import com.dicoding.storyapp.data.retrofit.ApiConfig
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getUser().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return StoryRepository.getInstance(apiService , pref)
    }
}