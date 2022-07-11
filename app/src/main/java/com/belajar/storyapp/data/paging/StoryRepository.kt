package com.belajar.storyapp.data.paging

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.belajar.storyapp.data.network.api.ApiService
import com.belajar.storyapp.data.network.response.StoriesResponse

class StoryRepository(private val apiService: ApiService) {
    fun getStory(token: String): LiveData<PagingData<StoriesResponse.Story>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10
            ),
            pagingSourceFactory = {
                StoryPagingSource(token, apiService)
            }
        ).liveData
    }
}