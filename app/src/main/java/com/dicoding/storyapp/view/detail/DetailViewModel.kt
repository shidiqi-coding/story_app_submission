package com.dicoding.storyapp.view.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.StoryRepository
import com.dicoding.storyapp.data.response.Story
import kotlinx.coroutines.launch

class DetailViewModel(private val repository: StoryRepository) : ViewModel() {

    private val _detailStories = MutableLiveData<Story>()
    val detailStories: LiveData<Story> = _detailStories

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun getDetailStory(id: String) {
        Log.d("DetailViewModel", "Getting detail for ID: $id")
        viewModelScope.launch {
            try {
                val story = repository.getStoryDetail(id)
                Log.d("DetailViewModel", "Story fetched: $story")
                _detailStories.value = story
                _error.value = null
            } catch (e: Exception) {
                Log.e("DetailViewModel", "Error: ${e.message}", e)
                _error.value = e.message
            }
        }
    }
}
