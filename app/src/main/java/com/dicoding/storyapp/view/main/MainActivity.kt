package com.dicoding.storyapp.view.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.storyapp.R
import com.dicoding.storyapp.ViewModelFactory
import com.dicoding.storyapp.databinding.ActivityMainBinding
import com.dicoding.storyapp.view.authenticator.login.LoginActivity
import com.dicoding.storyapp.view.detail.DetailActivity
import com.dicoding.storyapp.view.newstory.NewStoryActivity
import com.dicoding.storyapp.view.setting.SettingActivity

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var adapter: ListStoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        observeView()
        viewModel.fetchMainStories()
        binding.fabAddStory.setOnClickListener{
            val intent = Intent(this, NewStoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        adapter = ListStoryAdapter { storyId ->
            DetailActivity.start(this, storyId)
        }
        binding.rvStoryList.layoutManager = LinearLayoutManager(this)
        binding.rvStoryList.adapter = adapter
    }

    private fun observeView() {
        viewModel.storyList.observe(this) { stories ->
            adapter.submitList(stories)
        }

        viewModel.loading.observe(this) { isLoading ->
            binding.menuPB.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(this) { error ->
            if (!error.isNullOrEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                viewModel.clearError()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_setting -> {
                startActivity(Intent(this, SettingActivity::class.java))
                true
            }
            R.id.action_logout -> {
                showLogoutDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this).apply {
            setTitle("Konfirmasi Logout")
            setMessage("Apakah Anda yakin ingin keluar?")
            setPositiveButton("Ya") { _, _ ->
                val sharedPref = getSharedPreferences("session", MODE_PRIVATE)
                sharedPref.edit().clear().apply()
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            setNegativeButton("Batal", null)
            create()
            show()
        }
    }
}
