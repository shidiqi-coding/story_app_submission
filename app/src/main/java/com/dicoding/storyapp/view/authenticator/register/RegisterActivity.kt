package com.dicoding.storyapp.view.authenticator.register

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
import com.dicoding.storyapp.ViewModelFactory
import com.dicoding.storyapp.data.ResultState
import com.dicoding.storyapp.databinding.ActivityRegisterBinding
import com.dicoding.storyapp.view.WelcomeActivity
import com.dicoding.storyapp.R

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private var isPasswordVisible = false

    private val viewModel: RegisterViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        setupTogglePassword()

        binding.btnRegBack.setOnClickListener {
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.buttonRegister.setOnClickListener {
            val name = binding.UsernameInput.text.toString().trim()
            val email = binding.emailInput.text.toString().trim()
            val password = binding.passwordInput.text.toString().trim()

            if (name.isEmpty()) {
                binding.UsernameInput.error = "Nama tidak boleh kosong"
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                binding.emailInput.error = "Email tidak boleh kosong"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding.passwordInput.error = "Password tidak boleh kosong"
                return@setOnClickListener
            }

            viewModel.register(name, email, password).observe(this) { result ->
                when (result) {
                    is ResultState.Loading -> {
                        binding.loadingOverlay.visibility = View.VISIBLE
                    }

                    is ResultState.Success -> {
                        binding.loadingOverlay.visibility = View.GONE

                        AlertDialog.Builder(this).apply {
                            setTitle(getString(R.string.register_success_message))
                            setMessage("Akun $email telah berhasil dibuat. Silakan login.")
                            setPositiveButton("Lanjut") { _, _ ->
                                finish()
                            }
                            show()
                        }
                    }

                    is ResultState.Error -> {
                        AlertDialog.Builder(this).apply {
                            setTitle(getString(R.string.register_failed))
                            setMessage(result.error)
                            setPositiveButton(getString(R.string.ok_button), null)
                            show()
                        }
                    }
                }
            }
        }
    }

    private fun setupTogglePassword() {
        binding.ivToggleRegPassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                binding.passwordInput.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.ivToggleRegPassword.setImageResource(R.drawable.ic_visible)
            } else {
                binding.passwordInput.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.ivToggleRegPassword.setImageResource(R.drawable.ic_visibility_off)
            }
            binding.passwordInput.setSelection(binding.passwordInput.text?.length ?: 0)
        }
    }
}
