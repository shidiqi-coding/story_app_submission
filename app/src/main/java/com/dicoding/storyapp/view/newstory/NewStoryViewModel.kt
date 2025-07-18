package com.dicoding.storyapp.view.newstory


import android.content.Context
import androidx.lifecycle.ViewModel
import com.dicoding.storyapp.StoryRepository
import java.io.File

class NewStoryViewModel(private val repository: StoryRepository) : ViewModel() {

   fun uploadImage(imageFile: File, description: String, context: Context) =
      repository.uploadImage(imageFile, description, context)

    

}