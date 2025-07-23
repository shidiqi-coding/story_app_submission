package com.dicoding.storyapp.view

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.storyapp.data.pref.UserPreference
import com.dicoding.storyapp.data.pref.dataStore
import com.dicoding.storyapp.databinding.ActivityWelcomeBinding
import com.dicoding.storyapp.view.authenticator.login.LoginActivity
import com.dicoding.storyapp.view.authenticator.register.RegisterActivity
import com.dicoding.storyapp.view.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding
    private lateinit var userPref: UserPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userPref = UserPreference.getInstance(dataStore)

        CoroutineScope(Dispatchers.Main).launch {
            val session = userPref.getSession().first()
            if (session.isLogin) {
                startActivity(Intent(this@WelcomeActivity , MainActivity::class.java))
                finish()
            } else {
                binding = ActivityWelcomeBinding.inflate(layoutInflater)
                setContentView(binding.root)
                setupView()
                setupAction()
                playAnimation()
            }
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.welcomeImage , View.TRANSLATION_X , -30f , 30f).apply {
            duration = 60000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val login = ObjectAnimator.ofFloat(binding.loginButton , View.ALPHA , 1f).setDuration(500)
        val register =
            ObjectAnimator.ofFloat(binding.registerButton , View.ALPHA , 1f).setDuration(500)
        val welcomeTitle =
            ObjectAnimator.ofFloat(binding.welcomeTitle , View.ALPHA , 1f).setDuration(500)
        val welcomeDesc =
            ObjectAnimator.ofFloat(binding.welcomeDesc , View.ALPHA , 1f).setDuration(500)


        val together = AnimatorSet().apply {
            playTogether(login , register)
        }

        AnimatorSet().apply {
            playSequentially(welcomeTitle, welcomeDesc, together)
            start()
        }


    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN ,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            startActivity(Intent(this , LoginActivity::class.java))
        }

        binding.registerButton.setOnClickListener {
            startActivity(Intent(this , RegisterActivity::class.java))
        }
    }
}
