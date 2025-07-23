package com.dicoding.storyapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.dicoding.storyapp.view.WelcomeActivity
import com.dicoding.storyapp.view.authenticator.login.LoginActivity
import com.dicoding.storyapp.view.main.MainActivity
import com.dicoding.storyapp.view.setting.SettingPreferences
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        val prefs = SettingPreferences.getInstance(this)
        lifecycleScope.launch {
            prefs.getThemeSetting().collect { themeValue ->
                applyTheme(themeValue)
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v , insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left , systemBars.top , systemBars.right , systemBars.bottom)
            insets
        }
        Handler(Looper.getMainLooper()).postDelayed(
            {
                val onboardingPref= getSharedPreferences("onBoarding",MODE_PRIVATE)
                val userPref = getSharedPreferences("user_session",MODE_PRIVATE)

                val isFirstRun = onboardingPref.getBoolean("isFirstRun", true)
                val isLoggedIn = userPref.getBoolean("isLoggedIn",false)

               when {
                   isFirstRun -> {
                       onboardingPref.edit().putBoolean("isFirstRun",true).apply()
                       startActivity(Intent(this, WelcomeActivity::class.java))

                   }

                   isLoggedIn -> {
                       startActivity(Intent(this, MainActivity::class.java))
                   }

                   else -> {
                       startActivity(Intent(this, LoginActivity::class.java))
                   }
               }
               finish()

            } , 3000
        )
        supportActionBar?.hide()
    }

    private fun applyTheme(choice: Int) {
        when (choice) {
            0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }

}