package com.belajar.storyapp.data.network.api

import com.belajar.storyapp.model.LoginBody
import com.belajar.storyapp.model.RegisterBody
import com.belajar.storyapp.data.network.response.AuthResponse
import com.belajar.storyapp.data.network.response.StoriesResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

//
interface ApiService {

    @POST("register")
    fun register(
        @Body registerBody: RegisterBody
    ): Call<AuthResponse>

    @POST("login")
    fun login(
        @Body loginBody: LoginBody
    ): Call<AuthResponse>

    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("location") location: Int? = null
    ): StoriesResponse

    @GET("stories")
    fun getStoriesMaps(
        @Header("Authorization") token: String,
        @Query("size") size: Int = 1000,
        @Query("location") location: Int = 1
    ): Call<StoriesResponse>

    @Multipart
    @POST("stories")
    fun postStories(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Query("lat") lat: RequestBody?,
        @Query("lon") lon: RequestBody?
    ): Call<StoriesResponse>
}