package com.belajar.storyapp.ui.splash

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.belajar.storyapp.R
import com.belajar.storyapp.const.Constant.SPLASH_LOADING
import com.belajar.storyapp.ui.login.LoginActivity
import com.belajar.storyapp.ui.main.MainActivity
import com.belajar.storyapp.ui.main.MainActivity.Companion.EXTRA_TOKEN_MAIN
import com.belajar.storyapp.util.AppPreferences
import com.belajar.storyapp.util.PreferencesModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var viewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        supportActionBar?.hide()

        setupViewModel()
        getPreference()
    }

    private fun setupViewModel() {
        val pref = AppPreferences.getInstance(dataStore)
        viewModel = ViewModelProvider(this, PreferencesModelFactory(pref))[SplashViewModel::class.java]
    }

    private fun getPreference() {
        val handler = Handler(mainLooper)
        handler.postDelayed({
            viewModel.getIsDarkMode().observe(this@SplashActivity) { isDarkModeActive->
                if (isDarkModeActive == true) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
            viewModel.getUserToken().observe(this@SplashActivity) { token ->
                if (token.isNullOrEmpty()) {
                    Intent(this@SplashActivity, LoginActivity::class.java).also { intent ->
                        startActivity(intent)
                        finish()
                    }
                } else {
                    Intent(this@SplashActivity, MainActivity::class.java).also { intent ->
                        intent.putExtra(EXTRA_TOKEN_MAIN, token)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }, SPLASH_LOADING)
    }
}