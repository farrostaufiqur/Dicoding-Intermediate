package com.belajar.storyapp.ui.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.belajar.storyapp.databinding.ActivityProfileBinding
import com.belajar.storyapp.ui.login.LoginActivity
import com.belajar.storyapp.util.AppPreferences
import com.belajar.storyapp.util.PreferencesModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class ProfileActivity : AppCompatActivity() {
    private var _binding: ActivityProfileBinding? = null
    private val binding get() = _binding
    private var userId = ""
    private var userName = ""
    private var userEmail = ""
    private var isDark: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(_binding?.root)

        val pref = AppPreferences.getInstance(dataStore)
        val viewModel = ViewModelProvider(this, PreferencesModelFactory(pref))[ProfileViewModel::class.java]
        viewModel.getUserId().observe(this@ProfileActivity){
            if (it != null) {
                userId = it
            }
        }
        viewModel.getUserName().observe(this@ProfileActivity){
            if (it != null) {
                userName = it
            }
        }
        viewModel.getUserEmail().observe(this@ProfileActivity){
            if (it != null) {
                userEmail = it
            }
        }
        binding?.apply {
            tvProfileName.text = userName
            tvProfileEmail.text = userEmail
            tvProfileUserid.text = userId
            Log.d(TAG, "Name: $userName, Email: $userEmail, Id: $userId")
            btnLogout.setOnClickListener {
                viewModel.clearUserData()
                Intent(this@ProfileActivity, LoginActivity::class.java).also { intent ->
                    startActivity(intent)
                    finish()
                }
            }
            btnChangeTheme.setOnClickListener {
                viewModel.getIsDarkMode().observe(this@ProfileActivity){
                    if (it != null) {
                        isDark = it
                    }
                }

                isDark = !isDark

                viewModel.saveIsDarkMode(isDark)
                if (isDark) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
            btnChangeLang.setOnClickListener {
                val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(intent)
            }
        }
    }

    companion object{
        const val TAG = "ProfileActivity"
    }
}