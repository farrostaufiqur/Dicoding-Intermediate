package com.belajar.storyapp.ui.register

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.belajar.storyapp.model.RegisterBody
import com.belajar.storyapp.data.network.api.ApiConfig
import com.belajar.storyapp.data.network.response.AuthResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel: ViewModel() {

    private val _isRegistered = MutableLiveData<Boolean>()
    val isRegistered: MutableLiveData<Boolean> = _isRegistered

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: MutableLiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<Boolean>()
    val isError: MutableLiveData<Boolean> = _isError

    fun register(body : RegisterBody){
        _isLoading.value = true
        val client= ApiConfig.getApiService().register(body)
        client.enqueue(object : Callback<AuthResponse> {
            override fun onResponse(
                call: Call<AuthResponse>,
                response: Response<AuthResponse>
            ){
                _isLoading.value = false
                _isError.value = false
                _isRegistered.value = false

                if(response.isSuccessful) {
                    val message = "onSuccess: ${response.body()!!.message}, ${response.body()!!.error}"
                    Log.d(TAG, message)
                    _isRegistered.value = true
                }else{
                    val message = "onFailure: ${response.message()}"
                    _isError.value = true
                    Log.e(TAG, message)
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                val message = "onFailure: ${t.message.toString()}"
                _isError.value = true
                Log.e(TAG, message)
            }
        })
    }
    companion object{
        private const val TAG = "RegisterVM"
    }
}