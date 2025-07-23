package com.dicoding.storyapp.view.newstory



import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dicoding.storyapp.StoryRepository
import com.dicoding.storyapp.data.ResultState
import com.dicoding.storyapp.data.response.UploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody


class NewStoryViewModel(private val repository: StoryRepository) : ViewModel() {



   fun uploadStory(token: String,image: MultipartBody.Part, description: RequestBody, lat: RequestBody? = null, lon: RequestBody? = null): LiveData<ResultState<UploadResponse>> {
      return repository.uploadStory(token,image, description, lat,lon)
   }

   fun getSession() = repository.getSession()
}
