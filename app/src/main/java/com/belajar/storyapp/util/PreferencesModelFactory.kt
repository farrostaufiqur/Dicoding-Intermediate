package com.belajar.storyapp.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory
import com.belajar.storyapp.ui.login.LoginViewModel
import com.belajar.storyapp.ui.maps.MapsViewModel
import com.belajar.storyapp.ui.settings.SettingsViewModel
import com.belajar.storyapp.ui.splash.SplashViewModel

@Suppress("UNCHECKED_CAST")
class PreferencesModelFactory(private val pref: AppPreferences) : NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(pref) as T
        }else if (modelClass.isAssignableFrom(SplashViewModel::class.java)) {
            return SplashViewModel(pref) as T
        }else if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(pref) as T
        }else if (modelClass.isAssignableFrom(MapsViewModel::class.java)) {
            return MapsViewModel(pref) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}