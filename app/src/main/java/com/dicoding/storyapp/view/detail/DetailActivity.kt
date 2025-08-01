package com.dicoding.storyapp.view.detail

import android.content.Context
import android.content.Intent
import android.transition.Fade
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
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
        Log.d("DetailActivity", "Received Story ID: $storyId")


        if (storyId.isBlank()) {
            Log.e("DetailActivity", "Story ID is blank!")
            finish()
            return
        }

        binding.tvDetailImage.transitionName = "image_detail"
        binding.tvDetailName.transitionName = "title"
        binding.tvDetailDescription.transitionName = "description"

        window.enterTransition = Fade()
        window.exitTransition = Fade()

        binding.btnBack.setOnClickListener {
            supportFinishAfterTransition()
        }


        val factory = ViewModelFactory.getInstance(applicationContext)
        viewModel = ViewModelProvider(this, factory)[DetailViewModel::class.java]


        viewModel.getDetailStory(storyId)
        observeStory()

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun observeStory() {
        viewModel.detailStories.observe(this) { story ->
            Log.d("DetailActivity", "Story Data: $story")

            binding.tvDetailName.text = story.name
            binding.tvDetailDescription.text = story.description

            Glide.with(this)
                .load(story.photoUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        supportStartPostponedEnterTransition()
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        supportStartPostponedEnterTransition()
                        return false
                    }
                })
                .into(binding.tvDetailImage)
        }
    }

    companion object {
        const val EXTRA_STORY_ID = "extra_story_id"
//        const val EXTRA_ID = "extra_id"

        fun start(context: Context, storyId: String) {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(EXTRA_STORY_ID, storyId)
            context.startActivity(intent)
        }
    }
}
