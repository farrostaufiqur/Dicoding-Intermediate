package com.belajar.storyapp.ui.story.upload

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.belajar.storyapp.data.network.api.ApiConfig
import com.belajar.storyapp.data.network.response.StoriesResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UploadStoryViewModel : ViewModel() {

    private val _isUploaded = MutableLiveData<Boolean>()
    val isUploaded: LiveData<Boolean> = _isUploaded

    private val _responseMessage = MutableLiveData<String>()
    val responseMessage: LiveData<String> = _responseMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError

    fun postStories(token: String, image: MultipartBody.Part, description: RequestBody){
        _isLoading.value = true
        val client= ApiConfig.getApiService().postStories(token, image, description)
        client.enqueue(object : Callback<StoriesResponse> {
            override fun onResponse(
                call: Call<StoriesResponse>,
                response: Response<StoriesResponse>
            ){
                _isLoading.value = false
                _isError.value = false
                _isUploaded.value = false

                if(response.isSuccessful) {
                    val responseBody = response.body()!!
                    val message = "onFailure: ${responseBody.message}"
                    _responseMessage.value = message
                    _isError.value = false
                    _isUploaded.value = true

                } else {
                    val message = "onFailure: ${response.message()}"
                    _responseMessage.value = message
                    _isError.value = true
                    Log.e(TAG, message)
                }
            }

            override fun onFailure(call: Call<StoriesResponse>, t: Throwable) {
                val message = "onFailure: ${t.message.toString()}"
                _responseMessage.value = message
                _isError.value = true
                Log.e(TAG, message)
            }
        })
    }
    companion object{
        private const val TAG = "UploadVM"
    }
}