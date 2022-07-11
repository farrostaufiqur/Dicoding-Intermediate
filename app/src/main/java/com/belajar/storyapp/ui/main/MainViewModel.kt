package com.belajar.storyapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.belajar.storyapp.data.network.response.StoriesResponse
import com.belajar.storyapp.data.paging.StoryRepository

class MainViewModel constructor(private val storyRepository: StoryRepository) : ViewModel() {
    fun getStory(token: String): LiveData<PagingData<StoriesResponse.Story>> =
        storyRepository.getStory(token).cachedIn(viewModelScope)
}