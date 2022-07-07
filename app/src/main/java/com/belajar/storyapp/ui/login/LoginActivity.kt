package com.belajar.storyapp.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.belajar.storyapp.R.string
import com.belajar.storyapp.const.Constant.VIEW_DELAY
import com.belajar.storyapp.const.Constant.VIEW_DURATION
import com.belajar.storyapp.databinding.ActivityLoginBinding
import com.belajar.storyapp.model.LoginBody
import com.belajar.storyapp.ui.main.MainActivity
import com.belajar.storyapp.ui.register.RegisterActivity
import com.belajar.storyapp.util.AppPreferences
import com.belajar.storyapp.util.PreferencesModelFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class LoginActivity : AppCompatActivity() {
    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding
    private var login: Job = Job()
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(_binding?.root)

        setupViewModel()
        setupAction()
        startAnimation()
    }

    private fun setupViewModel() {
        val pref = AppPreferences.getInstance(dataStore)
        viewModel = ViewModelProvider(this, PreferencesModelFactory(pref))[LoginViewModel::class.java]
    }

    private fun setupAction() {
        binding?.apply {
            btnLogin.setOnClickListener {
                lifecycleScope.launchWhenResumed {
                    if (login.isActive) login.cancel()
                    login = launch {
                        val email = edtLoginEmail.text.toString().trim()
                        val password = edtLoginPassword.text.toString().trim()
                        when {
                            email.isBlank() -> edtLoginEmail.error = getString(string.empty_email)
                            password.isBlank() -> edtLoginPassword.error = getString(string.empty_password)
                            else -> {
                                Log.e(TAG,"Email: $email, Password: $password")
                                val body = LoginBody(
                                    email, password
                                )
                                viewModel.login(body)

                                viewModel.isLoading.observe(this@LoginActivity) {
                                    showLoading(it)
                                }

                                viewModel.isError.observe(this@LoginActivity){
                                    if (it == true){
                                        showToast(getString(string.login_failed))
                                    }
                                }

                                viewModel.isToken.observe(this@LoginActivity) {
                                    if (it != null){
                                        showToast(getString(string.login_success))
                                        Intent(this@LoginActivity, MainActivity::class.java).also { intent ->
                                            intent.putExtra(MainActivity.EXTRA_TOKEN, it)
                                            startActivity(intent)
                                            finish()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            registerHere.setOnClickListener {
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun startAnimation() {
        binding?.apply {
            ObjectAnimator.ofFloat(loginTitle, View.TRANSLATION_X, 0f, 50f).apply {
                duration = VIEW_DURATION
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
            }.start()

            val loginTitle = ObjectAnimator.ofFloat(loginTitle, View.ALPHA, 1f).setDuration(
                VIEW_DELAY)
            val loginDesc = ObjectAnimator.ofFloat(loginDesc, View.ALPHA, 1f).setDuration(VIEW_DELAY)
            val emailTitle = ObjectAnimator.ofFloat(emailTitle, View.ALPHA, 1f).setDuration(VIEW_DELAY)
            val emailEdt = ObjectAnimator.ofFloat(edtLoginEmail, View.ALPHA, 1f).setDuration(VIEW_DELAY)
            val passwordTitle = ObjectAnimator.ofFloat(passwordTitle, View.ALPHA, 1f).setDuration(VIEW_DELAY)
            val passwordEdt = ObjectAnimator.ofFloat(edtLoginPassword, View.ALPHA, 1f).setDuration(VIEW_DELAY)
            val loginBtn = ObjectAnimator.ofFloat(btnLogin, View.ALPHA, 1f).setDuration(VIEW_DELAY)
            val regist = ObjectAnimator.ofFloat(regist, View.ALPHA, 1f).setDuration(VIEW_DELAY)

            val emailPart = AnimatorSet().apply {
                playTogether(emailTitle, emailEdt)
            }

            val passwordPart = AnimatorSet().apply {
                playTogether(passwordTitle, passwordEdt)
            }

            AnimatorSet().apply {
                playSequentially(loginTitle, loginDesc, emailPart, passwordPart, loginBtn, regist)
                start()
            }

        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.apply {
            pbLogin.visibility = if (isLoading) View.VISIBLE else View.GONE
            edtLoginEmail.isClickable = !isLoading
            edtLoginEmail.isEnabled = !isLoading
            edtLoginPassword.isClickable = !isLoading
            edtLoginPassword.isEnabled = !isLoading
            btnLogin.isClickable = !isLoading
        }
    }

    private fun showToast(text: String) {
        Toast.makeText(this@LoginActivity,
            text, Toast.LENGTH_SHORT).show()
    }

    companion object{
        private const val TAG = "LoginActivity"
    }
}