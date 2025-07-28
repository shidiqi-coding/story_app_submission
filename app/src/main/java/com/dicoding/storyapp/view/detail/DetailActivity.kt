package com.dicoding.storyapp.view.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.dicoding.storyapp.ViewModelFactory
import com.dicoding.storyapp.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var viewModel: DetailViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val storyId = intent.getStringExtra(EXTRA_STORY_ID) ?: ""

        val factory = ViewModelFactory.getInstance(applicationContext)
        viewModel = ViewModelProvider(this, factory)[DetailViewModel::class.java]
        viewModel.getDetailStory(storyId)

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }


        observeStory()


    }


    private fun  observeStory() {
        viewModel.detailStories.observe(this) { story ->
            with(binding) {
                binding.tvDetailName.text = story.name
                binding.tvDetailDescription.text = story.description

                Glide.with(this@DetailActivity)
                    .load(story.photoUrl)
                    .into(tvDetailImage)


            }
        }

    }


    companion object {

        const val EXTRA_STORY_ID = "extra_story_id"
        fun start(context: Context , storyId: String) {
            val intent = Intent(context , DetailActivity::class.java)
            intent.putExtra(EXTRA_STORY_ID , storyId)
            context.startActivity(intent)
        }
    }
}
