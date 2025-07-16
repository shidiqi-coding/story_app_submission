package com.dicoding.storyapp.view.authenticator.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dicoding.storyapp.StoryRepository

class RegisterViewModel(private val repository :StoryRepository): ViewModel()
{
    fun register(name : String, email: String, password: String) =
        repository.register(name, email, password)
}