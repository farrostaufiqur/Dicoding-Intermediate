package com.belajar.storyapp.util

import android.content.Context
import com.belajar.storyapp.data.local.StoryDatabase
import com.belajar.storyapp.data.network.api.ApiConfig
import com.belajar.storyapp.data.paging.StoryRepository

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return StoryRepository(database, apiService)
    }
}