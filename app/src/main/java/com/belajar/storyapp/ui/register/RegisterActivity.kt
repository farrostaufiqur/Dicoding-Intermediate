package com.belajar.storyapp.ui.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.belajar.storyapp.R.string
import com.belajar.storyapp.const.Constant.VIEW_DELAY
import com.belajar.storyapp.const.Constant.VIEW_DURATION
import com.belajar.storyapp.databinding.ActivityRegisterBinding
import com.belajar.storyapp.model.RegisterBody
import com.belajar.storyapp.ui.login.LoginActivity
import com.belajar.storyapp.util.isEmailValid
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private val viewModel: RegisterViewModel by viewModels()
    private var register: Job = Job()
    private var _binding: ActivityRegisterBinding? = null
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(_binding?.root)

        setupAction()
        startAnimation()
    }

    private fun setupAction() {
        binding?.apply {
            lifecycleScope.launchWhenResumed {
                if (register.isActive) register.cancel()
                register = launch {

                    btnRegister.setOnClickListener {
                        val name = edtRegisterName.text.toString()
                        val email = edtRegisterEmail.text.toString()
                        val password = edtRegisterPassword.text.toString()
                        when {
                            name.isBlank() -> edtRegisterName.error = getString(string.empty_name)
                            email.isBlank() -> edtRegisterEmail.error = getString(string.empty_email)
                            !email.isEmailValid() -> edtRegisterEmail.error = getString(string.invalid_email)
                            password.isBlank() -> edtRegisterPassword.error = getString(string.empty_password)
                            else -> {
                                Log.e(TAG,"Name: $name, Email: $email, Password: $password")
                                val body = RegisterBody(
                                    name, email, password
                                )
                                viewModel.register(body)
                            }
                        }

                        viewModel.isError.observe(this@RegisterActivity){
                            if (it == true){
                                showToast(getString(string.register_failed))
                            }
                        }

                        viewModel.isLoading.observe(this@RegisterActivity) {
                            showLoading(it)
                        }

                        viewModel.isRegistered.observe(this@RegisterActivity) {
                            if (it == true){
                                showToast(getString(string.register_success))
                                val intent = Intent (this@RegisterActivity, LoginActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                    }
                }
            }
            loginHere.setOnClickListener {
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun startAnimation() {
        binding?.apply {
            ObjectAnimator.ofFloat(registTitle, View.TRANSLATION_X, 0f, 50f).apply {
                duration = VIEW_DURATION
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
            }.start()

            val registTitle = ObjectAnimator.ofFloat(registTitle, View.ALPHA, 1f).setDuration(
                VIEW_DELAY)
            val registDesc = ObjectAnimator.ofFloat(registDesc, View.ALPHA, 1f).setDuration(VIEW_DELAY)
            val nameTitle = ObjectAnimator.ofFloat(nameTitle, View.ALPHA, 1f).setDuration(VIEW_DELAY)
            val nameEdt = ObjectAnimator.ofFloat(edtRegisterName, View.ALPHA, 1f).setDuration(VIEW_DELAY)
            val emailTitle = ObjectAnimator.ofFloat(emailTitle, View.ALPHA, 1f).setDuration(VIEW_DELAY)
            val emailEdt = ObjectAnimator.ofFloat(edtRegisterEmail, View.ALPHA, 1f).setDuration(VIEW_DELAY)
            val passwordTitle = ObjectAnimator.ofFloat(passwordTitle, View.ALPHA, 1f).setDuration(VIEW_DELAY)
            val passwordEdt = ObjectAnimator.ofFloat(edtRegisterPassword, View.ALPHA, 1f).setDuration(VIEW_DELAY)
            val registBtn = ObjectAnimator.ofFloat(btnRegister, View.ALPHA, 1f).setDuration(VIEW_DELAY)
            val log = ObjectAnimator.ofFloat(login, View.ALPHA, 1f).setDuration(VIEW_DELAY)

            val namePart = AnimatorSet().apply {
                playTogether(nameTitle, nameEdt)
            }

            val emailPart = AnimatorSet().apply {
                playTogether(emailTitle, emailEdt)
            }

            val passwordPart = AnimatorSet().apply {
                playTogether(passwordTitle, passwordEdt)
            }

            AnimatorSet().apply {
                playSequentially(registTitle, registDesc, namePart, emailPart, passwordPart, registBtn, log)
                start()
            }

        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this@RegisterActivity,
            message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.apply {
            pbRegister.visibility = if (isLoading) View.VISIBLE else View.GONE
            edtRegisterName.isClickable = !isLoading
            edtRegisterName.isEnabled = !isLoading
            edtRegisterEmail.isClickable = !isLoading
            edtRegisterEmail.isEnabled = !isLoading
            edtRegisterPassword.isClickable = !isLoading
            edtRegisterPassword.isEnabled = !isLoading
            btnRegister.isClickable = !isLoading
        }
    }

    companion object{
        const val TAG = "RegisterActivity"
    }
}