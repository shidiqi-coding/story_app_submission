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
import androidx.appcompat.app.AppCompatDelegate
import com.dicoding.storyapp.data.pref.dataStore
import com.dicoding.storyapp.data.pref.UserPreference
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.storyapp.R
import com.dicoding.storyapp.ViewModelFactory
import com.dicoding.storyapp.view.setting.SettingPreferences
import com.dicoding.storyapp.databinding.ActivityMainBinding
import com.dicoding.storyapp.view.WelcomeActivity
import com.dicoding.storyapp.view.detail.DetailActivity
import com.dicoding.storyapp.view.newstory.NewStoryActivity
import com.dicoding.storyapp.view.setting.SettingActivity
import kotlinx.coroutines.launch

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

        val prefs = SettingPreferences.getInstance(this)
        lifecycleScope.launch {
            prefs.getThemeSetting().collect { themeValue ->
                applyTheme(themeValue)
            }
        }

        setupRecyclerView()
        observeView()
        setUpGreeting()
        viewModel.fetchMainStories()

        binding.fabAddStory.setOnClickListener {
            val intent = Intent(this , NewStoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setUpGreeting() {
        val userPref = UserPreference.getInstance(dataStore)
        lifecycleScope.launch {
            userPref.getSession().collect { user ->
                val greeting = getString(R.string.greeting, user.name)
                binding.greetingId.text = greeting
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = ListStoryAdapter { storyId ->
            DetailActivity.start(this , storyId)
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
                Toast.makeText(this , error , Toast.LENGTH_SHORT).show()
                viewModel.clearError()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_list , menu)
        if (menu != null) {
            if (menu.javaClass.simpleName == "MenuBuilder") {
                try {
                    val method = menu.javaClass.getDeclaredMethod(
                        "setOptionalIconsVisible" ,
                        Boolean::class.javaPrimitiveType
                    )
                    method.isAccessible = true
                    method.invoke(menu , true)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_setting -> {
                startActivity(Intent(this , SettingActivity::class.java))
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
        val alertDialog = AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.logout_popup_text))
            setMessage(getString(R.string.logout_message))
            setPositiveButton(getString(R.string.yes_button)) { _ , _ ->
                lifecycleScope.launch {
                    val userPref = UserPreference.getInstance(dataStore)
                    userPref.logout()
                    val intent = Intent(this@MainActivity , WelcomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }
            setNegativeButton(getString(R.string.cancel_button) , null)
        }.create()


        alertDialog.setOnShowListener {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(getColor(R.color.button_color))

            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(getColor(R.color.button_color))
        }

        alertDialog.show()
    }

    private fun applyTheme(choice: Int) {
        when (choice) {
            0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchMainStories()
    }


}