package com.dicoding.storyapp.view.authenticator.register

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.storyapp.view.helper.LocaleHelper
import androidx.core.content.ContextCompat
import com.dicoding.storyapp.R
import com.dicoding.storyapp.ViewModelFactory
import com.dicoding.storyapp.data.ResultState
import com.dicoding.storyapp.databinding.ActivityRegisterBinding
import com.dicoding.storyapp.view.WelcomeActivity
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private var isPasswordVisible = false

    private val viewModel: RegisterViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun attachBaseContext(newBase: Context?) {
        val langCode = LocaleHelper.getSavedLanguage(newBase ?: return)
        val contextWithLocale = LocaleHelper.applyLanguage(newBase, langCode)
        super.attachBaseContext(contextWithLocale)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        setupTogglePassword()

        binding.btnRegBack.setOnClickListener {
            startActivity(Intent(this, WelcomeActivity::class.java))
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
            val username = binding.usernameInput.text.toString().trim()
            val email = binding.emailInput.getText().toString().trim()
            val password = binding.passwordInput.text.toString().trim()

            binding.usernameLayout.error = null
            binding.emailLayout.error = null
            binding.passwordInputLayout.error = null

            if (username.isEmpty()) {
                binding.usernameLayout.error = getString(R.string.required_name)
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                binding.emailLayout.error = getString(R.string.required_email)
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding.passwordInputLayout.error = getString(R.string.required_password)
                return@setOnClickListener
            }

            if (password.length < 8) {
                binding.passwordInput.error = getString(R.string.minimum_password)
                return@setOnClickListener
            }

            viewModel.register(username, email, password).observe(this) { result ->
                when (result) {
                    is ResultState.Loading -> {
                        binding.loadingOverlay.visibility = View.VISIBLE
                    }

                    is ResultState.Success -> {
                        binding.loadingOverlay.visibility = View.GONE
                        val successMessage = String.format(
                            getString(R.string.register_success_message),
                            email
                        )

                        val dialog = AlertDialog.Builder(this).apply {
                            setTitle(getString(R.string.success_title))
                            setMessage(successMessage)
                            setPositiveButton(getString(R.string.continue_button)) { _, _ ->
                                finish()
                            }
                        }.create()

                        dialog.setOnShowListener {
                            dialog.findViewById<TextView>(android.R.id.message)?.setTextColor(
                                ContextCompat.getColor(
                                    this@RegisterActivity,
                                    R.color.text_color
                                )
                            )
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(
                                ContextCompat.getColor(
                                    this@RegisterActivity,
                                    R.color.button_text_color
                                )
                            )
                        }

                        dialog.show()
                    }

                    is ResultState.Error -> {
                        binding.loadingOverlay.visibility = View.GONE
                        val localizedMessage = try {
                            val json = JSONObject(result.error)
                            getLocalizedErrorMessage(json.getString("message"))
                        } catch (_: Exception) {
                            getLocalizedErrorMessage(result.error)
                        }

                        val dialog = AlertDialog.Builder(this).apply {
                            setTitle(getString(R.string.register_failed))
                            setMessage(localizedMessage)
                            setPositiveButton(getString(R.string.ok_button), null)
                        }.create()

                        dialog.setOnShowListener {
                            dialog.findViewById<TextView>(android.R.id.message)?.setTextColor(
                                ContextCompat.getColor(
                                    this@RegisterActivity,
                                    R.color.text_color
                                )
                            )
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(
                                ContextCompat.getColor(
                                    this@RegisterActivity,
                                    R.color.button_text_color
                                )
                            )
                        }

                        dialog.show()
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

    private fun getLocalizedErrorMessage(messageFromServer: String?): String {
        return when (messageFromServer) {
            "Email is already taken" -> getString(R.string.email_taken)
            else -> messageFromServer ?: getString(R.string.unknown_error)
        }
    }
}
