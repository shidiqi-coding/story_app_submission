package com.dicoding.storyapp.view.setting

import androidx.lifecycle.*
import kotlinx.coroutines.launch

class SettingViewModel(private val pref: SettingPreferences) : ViewModel() {

    val themeChoice: LiveData<Int> = pref.getThemeSetting().asLiveData()

    fun setThemeChoice(choice: Int) {
        viewModelScope.launch {
            pref.saveThemeSetting(choice)
        }
    }



    val languageSetting: LiveData<String?> = pref.getLanguageSetting().asLiveData()

    fun saveLanguageSetting(languageCode: String?) {
        viewModelScope.launch {
            pref.saveLanguageSetting(languageCode)
        }
    }
}
