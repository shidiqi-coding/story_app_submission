package com.dicoding.storyapp.view.authenticator.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.core.content.ContextCompat
import com.dicoding.storyapp.ViewModelFactory
import com.dicoding.storyapp.data.ResultState
import com.dicoding.storyapp.data.pref.UserModel
import com.dicoding.storyapp.databinding.ActivityLoginBinding
import com.dicoding.storyapp.view.WelcomeActivity
import com.dicoding.storyapp.view.main.MainActivity
import com.dicoding.storyapp.R
import android.util.Patterns

class LoginActivity : AppCompatActivity() {

    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityLoginBinding
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        setupTogglePassword()


        binding.btnLogBack.setOnClickListener {
            val intent = Intent(this , WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }


        binding.emailInput.addTextChangedListener {
            binding.emailInput.error = null
        }

        binding.passwordInput.addTextChangedListener {
            binding.passwordInput.error = null
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
        binding.buttonLogin.setOnClickListener {
            val email = binding.emailInput.text.toString().trim()
            val password = binding.passwordInput.text.toString().trim()
            var isValid = true


            if (email.isEmpty()) {
                binding.emailInput.error = getString(R.string.email_empty)
                isValid = false
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.emailInput.error = getString(R.string.email_invalid)
                isValid = false
            } else {
                binding.emailInput.error = null
            }


            if (password.isEmpty()) {
                binding.passwordInput.error = getString(R.string.password_empty)
                isValid = false
            } else {
                binding.passwordInput.error = null
            }

            if (!isValid) return@setOnClickListener

            viewModel.login(email, password).observe(this) { result ->
                when (result) {
                    is ResultState.Loading -> {
                        binding.loadingOverlay.visibility = View.VISIBLE
                    }

                    is ResultState.Success -> {
                        binding.loadingOverlay.visibility = View.GONE
                        val user = result.data


                        viewModel.saveSession(
                            UserModel(
                                email = email,
                                token = user.token ?: "",
                                isLogin = true,
                                name = user.name ?: ""
                            )

                        )


                        val dialog = AlertDialog.Builder(this).apply {
                            setTitle(getString(R.string.success_title))
                            setMessage(getString(R.string.login_success_message))
                            setPositiveButton(getString(R.string.continue_button)) { _, _ ->
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()
                            }
                        }.create()

                        dialog.setOnShowListener {
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                                .setTextColor(ContextCompat.getColor(this@LoginActivity, R.color.button_color))
                        }

                        dialog.show()
                    }

                    is ResultState.Error -> {
                        binding.loadingOverlay.visibility = View.GONE
                        AlertDialog.Builder(this).apply {
                            setTitle(getString(R.string.login_failed_title))
                            setMessage(result.error)
                            setPositiveButton(getString(R.string.ok_button), null)
                        }.show()
                    }
                }
            }
        }
    }


    private fun setupTogglePassword() {
        binding.ivToggleLogPassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                binding.passwordInput.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.ivToggleLogPassword.setImageResource(R.drawable.ic_visible)
            } else {
                binding.passwordInput.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.ivToggleLogPassword.setImageResource(R.drawable.ic_visibility_off)
            }


            binding.passwordInput.setSelection(binding.passwordInput.text?.length ?: 0)
        }
    }
}
