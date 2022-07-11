package com.belajar.storyapp.ui.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.belajar.storyapp.databinding.ActivitySettingsBinding
import com.belajar.storyapp.ui.login.LoginActivity
import com.belajar.storyapp.util.AppPreferences
import com.belajar.storyapp.util.PreferencesModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsActivity : AppCompatActivity() {
    private var _binding: ActivitySettingsBinding? = null
    private val binding get() = _binding

    private lateinit var viewModel: SettingsViewModel
    private var isDark: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(_binding?.root)

        val pref = AppPreferences.getInstance(dataStore)
        viewModel = ViewModelProvider(this, PreferencesModelFactory(pref))[SettingsViewModel::class.java]

        binding?.apply {
            btnLogout.setOnClickListener {
                viewModel.clearUserData()
                Intent(this@SettingsActivity, LoginActivity::class.java).also { intent ->
                    startActivity(intent)
                    finish()
                }
            }
            btnChangeTheme.setOnClickListener {
                viewModel.getIsDarkMode().observe(this@SettingsActivity){
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
}