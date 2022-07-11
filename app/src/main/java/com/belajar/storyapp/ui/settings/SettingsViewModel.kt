package com.belajar.storyapp.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.belajar.storyapp.util.AppPreferences
import kotlinx.coroutines.launch

class SettingsViewModel (private val pref: AppPreferences) : ViewModel() {
    fun getIsDarkMode(): LiveData<Boolean?> = pref.getIsDarkMode().asLiveData()

    fun saveIsDarkMode(isDark: Boolean) {
        viewModelScope.launch {
            pref.saveIsDarkMode(isDark)
        }
    }

    fun clearUserData(){
        viewModelScope.launch {
            pref.clearUserData()
        }
    }
}