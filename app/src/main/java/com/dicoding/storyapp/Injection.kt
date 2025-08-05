package com.dicoding.storyapp

import android.content.Context
import com.dicoding.storyapp.data.pref.UserPreference
import com.dicoding.storyapp.data.retrofit.ApiConfig


object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val pref = UserPreference.getInstance(context)
        val apiService = ApiConfig.getApiService()
        return StoryRepository.getInstance(apiService, pref, context)
    }
}
