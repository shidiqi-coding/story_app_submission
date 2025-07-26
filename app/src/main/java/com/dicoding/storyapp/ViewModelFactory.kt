package com.dicoding.storyapp

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.view.setting.SettingPreferences
import com.dicoding.storyapp.view.authenticator.login.LoginViewModel
import com.dicoding.storyapp.view.authenticator.register.RegisterViewModel
import com.dicoding.storyapp.view.detail.DetailViewModel
import com.dicoding.storyapp.view.main.MainViewModel
import com.dicoding.storyapp.view.newstory.NewStoryViewModel
import com.dicoding.storyapp.view.setting.SettingViewModel


class ViewModelFactory(private val repository: StoryRepository, private val pref: SettingPreferences) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository) as T
            }

            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repository) as T
            }

            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(repository) as T
            }



            modelClass.isAssignableFrom(NewStoryViewModel::class.java) -> {
                NewStoryViewModel(repository) as T
            }

            modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
                DetailViewModel(repository) as T
            }

            modelClass.isAssignableFrom(SettingViewModel::class.java) -> {
                SettingViewModel(pref) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory {
            return INSTANCE ?: synchronized(this) {
                val repository = Injection.provideRepository(context)
                val pref = SettingPreferences.getInstance(context)
                INSTANCE = ViewModelFactory(repository, pref)
                INSTANCE!!
            }
        }
    }

}