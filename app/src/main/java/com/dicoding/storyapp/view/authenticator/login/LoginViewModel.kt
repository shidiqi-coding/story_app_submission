package com.dicoding.storyapp.view.authenticator.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.StoryRepository
import com.dicoding.storyapp.data.ResultState
import com.dicoding.storyapp.data.pref.UserModel
import com.dicoding.storyapp.data.response.LoginResult
import kotlinx.coroutines.launch


class LoginViewModel(private val repository: StoryRepository) : ViewModel() {

    fun login(email: String, password: String): LiveData<ResultState<LoginResult>> {
        return repository.login(email, password)
    }


    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }

//    suspend fun logout() {
//        userPreference.logout()
//    }
}
