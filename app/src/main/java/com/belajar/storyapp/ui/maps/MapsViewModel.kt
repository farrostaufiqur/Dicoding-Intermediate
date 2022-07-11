package com.belajar.storyapp.ui.maps

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.belajar.storyapp.data.network.api.ApiConfig
import com.belajar.storyapp.data.network.response.StoriesResponse
import com.belajar.storyapp.util.AppPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsViewModel (private val pref: AppPreferences) : ViewModel() {

    private val _story = MutableLiveData<List<StoriesResponse.Story>>()
    val story: LiveData<List<StoriesResponse.Story>> = _story

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getAllStories(token: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getStoriesMaps(token)
        client.enqueue(object : Callback<StoriesResponse> {
            override fun onResponse(
                call: Call<StoriesResponse>,
                response: Response<StoriesResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _story.value = response.body()?.listStory
                    Log.e(TAG, "isSuccessful: ${response.body()?.message}")
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<StoriesResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    fun getUserToken(): LiveData<String?> = pref.getUserToken().asLiveData()
    fun getIsDarkMode(): LiveData<Boolean?> = pref.getIsDarkMode().asLiveData()

    companion object {
        private const val TAG = "MapsViewModel"

    }
}