package com.belajar.storyapp.data.network.response

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

class StoriesResponse (
    @field:SerializedName("error")
    val error: Boolean,
    @field:SerializedName("message")
    val message: String,
    @field:SerializedName("listStory")
    val listStory: List<Story>
){
    @Parcelize
    @Entity(tableName = "story")
    data class Story(

        @PrimaryKey
        @ColumnInfo(name = "id")
        @SerializedName("id")
        val id: String,

        @ColumnInfo(name = "name")
        @SerializedName("name")
        val name: String,

        @ColumnInfo(name = "description")
        @SerializedName("description")
        val description: String,

        @ColumnInfo(name = "photoUrl")
        @SerializedName("photoUrl")
        val photoUrl: String,

        @ColumnInfo(name = "createdAt")
        @SerializedName("createdAt")
        val createdAt: String,

        @ColumnInfo(name = "lon")
        @SerializedName("lon")
        val lon: Double?,

        @ColumnInfo(name = "lat")
        @SerializedName("lat")
        val lat: Double?
    ) :Parcelable
}