package com.dicoding.storyapp.view.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dicoding.storyapp.R

class DetailActivity : AppCompatActivity() {

    companion object {
        fun start(context: Context, storyId: String) {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("story_id", storyId)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val storyId = intent.getStringExtra("story_id")
        // gunakan storyId untuk fetch detail cerita
    }
}
