package com.belajar.storyapp.ui.main.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.belajar.storyapp.const.Constant
import com.belajar.storyapp.databinding.ItemStoryBinding
import com.belajar.storyapp.data.network.response.StoriesResponse
import com.belajar.storyapp.ui.story.detail.DetailStoryActivity
import com.belajar.storyapp.util.timeStamptoString
import com.bumptech.glide.Glide
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil

class StoryListAdapter : PagingDataAdapter<StoriesResponse.Story, StoryListAdapter.ViewHolder>(
    DIFF_CALLBACK
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(holder.itemView.context, data)
        }
    }

    class ViewHolder(private val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(context: Context, story: StoriesResponse.Story) {
            binding.apply {
                val lon = story.lon?.toBigDecimal()?.toPlainString()
                val lat = story.lat?.toBigDecimal()?.toPlainString()
                val location = "Lon: $lon, Lat: $lat"

                itemStoryName.text = story.name
                itemStoryLocation.text = location
                itemStoryDesc.text = story.description
                itemStoryTime.text = story.createdAt.timeStamptoString()
                Glide.with(itemView.context)
                    .load(story.photoUrl)
                    .into(itemStoryImg)

                root.setOnClickListener {
                    val optionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            root.context as Activity,
                            Pair(itemStoryImg, "image"),
                            Pair(itemStoryName, "name"),
                            Pair(itemStoryDesc, "desc"),
                            Pair(itemStoryTime, "time"),
                            Pair(itemStoryLocation, "loc")
                        )
                    val intent = Intent(it.context, DetailStoryActivity::class.java)
                    intent.putExtra(Constant.BUNDLE_STORY, story)
                    context.startActivity(intent, optionsCompat.toBundle())
                }
            }
        }
    }


    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoriesResponse.Story>() {
            override fun areItemsTheSame(oldItem: StoriesResponse.Story, newItem: StoriesResponse.Story): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: StoriesResponse.Story, newItem: StoriesResponse.Story): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}