package com.belajar.storyapp.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.belajar.storyapp.util.AppPreferences
import kotlinx.coroutines.launch

class ProfileViewModel (private val pref: AppPreferences) : ViewModel() {
    fun getUserId(): LiveData<String?> = pref.getUserId().asLiveData()
    fun getUserName(): LiveData<String?> = pref.getUserName().asLiveData()
    fun getUserEmail(): LiveData<String?> = pref.getUserEmail().asLiveData()
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