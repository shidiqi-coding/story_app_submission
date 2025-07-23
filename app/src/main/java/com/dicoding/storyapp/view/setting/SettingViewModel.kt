package com.dicoding.storyapp.view.setting

import androidx.lifecycle.*
import kotlinx.coroutines.launch

class SettingViewModel(private val pref: SettingPreferences) : ViewModel() {

    private val _themeChoice = MutableLiveData<Int>()
    val themeChoice: LiveData<Int> get() = _themeChoice

    init {
        viewModelScope.launch {
            pref.getThemeSetting().collect {
                _themeChoice.postValue(it)
            }
        }
    }

    fun getThemeSettings(): LiveData<Int> = pref.getThemeSetting().asLiveData()

    fun setThemeChoice(choice: Int) {
        _themeChoice.value = choice
        viewModelScope.launch {
            pref.saveThemeSetting(choice)
        }
    }
}
