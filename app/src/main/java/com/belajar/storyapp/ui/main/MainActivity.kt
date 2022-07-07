package com.belajar.storyapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.belajar.storyapp.R
import com.belajar.storyapp.databinding.ActivityMainBinding
import com.belajar.storyapp.ui.login.LoginActivity
import com.belajar.storyapp.ui.main.adapter.LoadingStateAdapter
import com.belajar.storyapp.ui.main.adapter.StoryListAdapter
import com.belajar.storyapp.ui.profile.ProfileActivity
import com.belajar.storyapp.ui.story.upload.UploadStoryActivity
import com.belajar.storyapp.ui.story.upload.UploadStoryActivity.Companion.EXTRA_TOKEN2
import com.belajar.storyapp.util.ViewModelFactory

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding
    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory(this)
    }
    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding?.root)
        isLoading(false)

        token = intent.getStringExtra(EXTRA_TOKEN)
        setupRecyclerView()
        getData()

        binding?.btnAddStory?.setOnClickListener {
            val intent = Intent(this@MainActivity, UploadStoryActivity::class.java)
            intent.putExtra(EXTRA_TOKEN2, token)
            startActivity(intent)
        }
        /*
        viewModel.isLoading.observe(this@MainActivity) {
            isLoading(it)
        }

        lifecycleScope.launchWhenResumed {
            if (main.isActive) main.cancel()
            main = launch {
                viewModel.getAllStories(token)
                viewModel.story.observe(this@MainActivity) {
                    setStoryData(it)
                }
            }
        }
        */
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)

        binding?.apply {
            rvStory.layoutManager = layoutManager
            rvStory.addItemDecoration(itemDecoration)
        }
    }

    private fun getData() {
        val adapter = StoryListAdapter()
        binding?.rvStory?.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
        if(token!=null){
            viewModel.getStory(token!!).observe(this) {
                adapter.submitData(lifecycle, it)
            }
        }

    }

    /*
    private fun setStoryData(listStory: List<StoriesResponse.Story>) {
        val list = ArrayList<StoriesResponse.Story>()
        for (storyId in listStory) {
            val story = StoriesResponse.Story(
                storyId.id,
                storyId.name,
                storyId.description,
                storyId.photoUrl,
                storyId.createdAt
            )
            list.add(story)
        }
        val storyAdapter = StoryListAdapter(list)
        binding?.rvStory?.adapter = storyAdapter
    }
    */
    private fun isLoading(loading: Boolean) {
        binding?.apply {
            if (loading) {
                rvStory.visibility = View.INVISIBLE
                shimmerLoading.visibility = View.VISIBLE
                shimmerLoading.startShimmer()
                pbMain.visibility = View.VISIBLE
            } else {
                rvStory.visibility = View.VISIBLE
                shimmerLoading.visibility = View.INVISIBLE
                shimmerLoading.stopShimmer()
                pbMain.visibility = View.INVISIBLE
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.profile -> {
                val intent = Intent(this@MainActivity, ProfileActivity::class.java)
                startActivity(intent)
                true
            }
            else -> false
        }
    }

    companion object {
        const val EXTRA_TOKEN = "token_extra"
    }
}