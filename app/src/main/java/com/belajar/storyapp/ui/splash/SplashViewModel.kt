package com.belajar.storyapp.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.belajar.storyapp.util.AppPreferences

class SplashViewModel (private val pref: AppPreferences) : ViewModel() {
    fun getUserToken(): LiveData<String?> = pref.getUserToken().asLiveData()
    fun getIsDarkMode(): LiveData<Boolean?> = pref.getIsDarkMode().asLiveData()
}