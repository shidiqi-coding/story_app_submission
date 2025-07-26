package com.dicoding.storyapp.view.detail

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

    fun getDetailStory(id: String) {
        viewModelScope.launch {
            try {
                val story = repository.getStoryDetail(id)
                _detailStories.value = story
            } catch (e: Exception) {
            }
        }
    }

}