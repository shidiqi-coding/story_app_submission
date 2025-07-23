package com.dicoding.storyapp.view.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.storyapp.R
import com.dicoding.storyapp.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var viewModel: DetailViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        val storyId = intent.getStringExtra(EXTRA_STORY_ID) ?: ""


        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        setStoriesData()


    }


   private fun setStoriesData() {

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
