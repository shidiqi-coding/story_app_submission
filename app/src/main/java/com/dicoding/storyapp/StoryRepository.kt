package com.dicoding.storyapp

import com.dicoding.storyapp.data.pref.UserPreference
import com.dicoding.storyapp.data.retrofit.ApiService

class StoryRepository(
    private val apiService: ApiService ,
    private val userPreference: UserPreference
) {
    companion object{
        @Volatile
        private var INSTANCE: StoryRepository? = null

        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference
        ) : StoryRepository {
            return  INSTANCE ?: synchronized(this) {
                val instance = StoryRepository(apiService, userPreference)
                INSTANCE = instance
                instance
            }
        }
                
    }
}