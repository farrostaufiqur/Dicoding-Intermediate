package com.belajar.storyapp.ui.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belajar.storyapp.model.LoginBody
import com.belajar.storyapp.data.network.api.ApiConfig
import com.belajar.storyapp.data.network.response.AuthResponse
import com.belajar.storyapp.util.AppPreferences
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel (private val pref: AppPreferences) : ViewModel() {

    private val _isToken = MutableLiveData<String>()
    val isToken: MutableLiveData<String> = _isToken

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: MutableLiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<Boolean>()
    val isError: MutableLiveData<Boolean> = _isError

    fun login(body: LoginBody){
        _isLoading.value = true
        val client= ApiConfig.getApiService().login(body)
        client.enqueue(object : Callback<AuthResponse> {
            override fun onResponse(
                call: Call<AuthResponse>,
                response: Response<AuthResponse>
            ){
                _isLoading.value = false
                _isError.value = false

                if(response.isSuccessful) {
                    val loginResult = response.body()!!.loginResult
                    val token = loginResult.token
                    val name = loginResult.name
                    val userId = loginResult.userId
                    val email = body.email

                    val bearerToken = "bearer $token"

                    Log.d(TAG, "Token: $token, Name: $name, Email: $email, UserId: $userId")

                    saveUserToken(bearerToken)

                    _isToken.value = bearerToken
                } else {
                    _isError.value = true
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                val message = "onFailure: ${t.message.toString()}"
                _isError.value = true
                Log.e(TAG, message)
                _isLoading.value = false
            }
        })
    }

    fun saveUserToken(token: String){
        viewModelScope.launch {
            pref.saveUserToken(token)
        }
    }

    companion object{
        private const val TAG = "LoginVM"
    }
}