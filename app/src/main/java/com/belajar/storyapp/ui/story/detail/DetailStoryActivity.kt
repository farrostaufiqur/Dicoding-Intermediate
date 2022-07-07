package com.belajar.storyapp.ui.story.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.belajar.storyapp.const.Constant
import com.belajar.storyapp.databinding.ActivityDetailStoryBinding
import com.belajar.storyapp.data.network.response.StoriesResponse
import com.belajar.storyapp.util.timeStamptoString
import com.bumptech.glide.Glide

class DetailStoryActivity : AppCompatActivity() {
    private var _binding: ActivityDetailStoryBinding? = null
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(_binding?.root)

        val story: StoriesResponse.Story? = intent.getParcelableExtra(Constant.BUNDLE_STORY)
        val lon = story?.lon?.toBigDecimal()?.toPlainString()
        val lat = story?.lat?.toBigDecimal()?.toPlainString()
        val location = "$lon, $lat"

        binding?.apply {
            Glide.with(this@DetailStoryActivity)
                .load(story?.photoUrl)
                .into(storyDetailImg)
            storyDetailName.text = story?.name
            storyDetailDesc.text = story?.description
            storyDetailTime.text = story?.createdAt?.timeStamptoString()
            storyDetailLocation.text = location
        }
    }
}