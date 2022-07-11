package com.belajar.storyapp.util

import com.belajar.storyapp.data.network.api.ApiConfig
import com.belajar.storyapp.data.paging.StoryRepository

object Injection {
    fun provideRepository(): StoryRepository {
        val apiService = ApiConfig.getApiService()
        return StoryRepository(apiService)
    }
}