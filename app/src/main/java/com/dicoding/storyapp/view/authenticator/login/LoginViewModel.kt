package com.dicoding.storyapp.view.authenticator.login

class LoginViewModel(private val repository: UserRepository): ViewModel() {
    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }
}