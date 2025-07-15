package com.dicoding.storyapp.view.main


import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.activity.viewModels
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
//import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.storyapp.databinding.ActivityMainBinding
import com.dicoding.storyapp.R
import com.dicoding.storyapp.view.authenticator.login.LoginActivity
import com.dicoding.storyapp.view.detail.DetailActivity
import com.dicoding.storyapp.view.setting.SettingActivity
import com.dicoding.storyapp.ViewModelFactory


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
    }

    private fun setupRecyclerView() {
        adapter = ListStoryAdapter { story ->
            DetailActivity.start(this , story)
        }
        binding.rvStoryList.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
    }

    private fun observeView() {
        viewModel.storyList.observe(this) { stories ->
            adapter.submitList(stories)
        }

        viewModel.loading.observe(this) { isLoading ->
            binding.menuPB.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

    }

    override fun onCreateOptionsMenu(menu:Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_list, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_setting -> {
                val intent = Intent(this , SettingActivity::class.java)
                startActivity(intent)
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
            setPositiveButton("Ya") { _ , _ ->
                val sharedPref = getSharedPreferences("session" , MODE_PRIVATE)
                sharedPref.edit().clear().apply()

                val intent = Intent(this@MainActivity , LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)


            }
            setNegativeButton("batal" , null)
            create()
            show()

        }

    }


}