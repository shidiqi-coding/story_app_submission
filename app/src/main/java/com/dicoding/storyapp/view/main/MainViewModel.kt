package com.dicoding.storyapp.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.StoryRepository
import com.dicoding.storyapp.data.response.ListStoryItem
import kotlinx.coroutines.launch

class MainViewModel(private val repository: StoryRepository): ViewModel(){

    private val _storyList= MutableLiveData<List<ListStoryItem>>()
    val storyList : LiveData<List<ListStoryItem>> = _storyList

    private val _loading= MutableLiveData<Boolean>()
        val loading : LiveData<Boolean> = _loading


    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage


    fun fetchMainStories() {
        _loading.value = true
        viewModelScope.launch {
            try {
                val result = repository.getStories()
                _storyList.value = result
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Terjadi kesalahan saat memuat data"
                _storyList.value = emptyList()
            } finally {
                _loading.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

//    suspend fun logout() {
//        userPreference.logout()
//    }


}