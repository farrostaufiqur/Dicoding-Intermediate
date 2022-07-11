package com.belajar.storyapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.belajar.storyapp.R
import com.belajar.storyapp.data.network.response.StoriesResponse
import com.belajar.storyapp.databinding.ActivityMainBinding
import com.belajar.storyapp.ui.main.adapter.LoadingStateAdapter
import com.belajar.storyapp.ui.main.adapter.StoryListAdapter
import com.belajar.storyapp.ui.maps.MapsActivity
import com.belajar.storyapp.ui.settings.SettingsActivity
import com.belajar.storyapp.ui.story.upload.UploadStoryActivity
import com.belajar.storyapp.ui.story.upload.UploadStoryActivity.Companion.EXTRA_TOKEN_UPLOAD
import com.belajar.storyapp.util.StoryModelFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var storyListAdapter: StoryListAdapter
    private lateinit var recyclerView: RecyclerView

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding
    private val viewModel: MainViewModel by viewModels { StoryModelFactory() }

    private var main: Job = Job()
    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding?.root)

        token = intent.getStringExtra(EXTRA_TOKEN_MAIN)

        lifecycleScope.launchWhenResumed {
            if (main.isActive) main.cancel()
            main = launch {
                setupRecyclerView()
                getData()
            }
        }

        binding?.btnAddStory?.setOnClickListener {
            val intent = Intent(this@MainActivity, UploadStoryActivity::class.java)
            intent.putExtra(EXTRA_TOKEN_UPLOAD, token)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        storyListAdapter = StoryListAdapter()
        recyclerView = binding?.rvStory!!
        val linearLayoutManager = LinearLayoutManager(this@MainActivity)
        val itemDecoration = DividerItemDecoration(this@MainActivity, linearLayoutManager.orientation)

        recyclerView.apply {
            layoutManager = linearLayoutManager
            addItemDecoration(itemDecoration)
            adapter = storyListAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    storyListAdapter.retry()
                }
            )
        }
    }

    private fun getData() {
        viewModel.getStory(token!!).observe(this) {
            updateRecyclerViewData(it)
        }
    }

    private fun updateRecyclerViewData(stories: PagingData<StoriesResponse.Story>) {
        val recyclerViewState = recyclerView.layoutManager?.onSaveInstanceState()
        storyListAdapter.submitData(lifecycle, stories)
        recyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings -> {
                val intent = Intent(this@MainActivity, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.maps -> {
                val intent = Intent(this@MainActivity, MapsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> false
        }
    }

    override fun onBackPressed() {
        finish()
    }

    companion object {
        const val EXTRA_TOKEN_MAIN = "token in main activity"
    }
}