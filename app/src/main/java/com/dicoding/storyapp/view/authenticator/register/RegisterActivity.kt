package com.dicoding.storyapp.view.authenticator.register

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.storyapp.databinding.ActivityRegisterBinding
import com.dicoding.storyapp.view.WelcomeActivity
import com.dicoding.storyapp.R


class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private var isPasswordVisible = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        setupTogglePassword()

        binding.btnRegBack.setOnClickListener {
            val intent = Intent(this , WelcomeActivity::class.java)
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
                WindowManager.LayoutParams.FLAG_FULLSCREEN ,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.buttonRegister.setOnClickListener {
            //val name = binding.nameInput.text.toString()
            val email = binding.emailInput.text.toString()
            //val password = binding.passwordInput.text.toString()

            AlertDialog.Builder(this).apply {
                setTitle("Selamat!")
                setMessage("Akun $email telah berhasil dibuat. Yuk, berbagi cerita dan pengalaman Anda.")
                setPositiveButton("Lanjut") { _ , _ ->
                    finish()
                }
                create()
                show()
            }

//            lifecycleScope.launch {
//                try {
//                    val response = registerViewModel.register(name, email, password)
//                    if (!response.error) {
//                        AlertDialog.Builder(this@RegisterActivity).apply {
//                            setTitle("Selamat!")
//                            setMessage("Akun $email telah berhasil dibuat. Yuk, berbagi cerita dan pengalaman Anda.")
//                            setPositiveButton("Lanjut") { _, _ ->
//                                finish()
//                            }
//                            show()
//                        }
//                    } else {
//                        showErrorDialog(response.message)
//                    }
//                } catch (e: HttpException) {
//                    val jsonInString = e.response()?.errorBody()?.string()
//                    val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
//                    showErrorDialog(errorBody.message)
//                }
//            }
        }
    }

//    private fun showErrorDialog(message: String) {
//        AlertDialog.Builder(this).apply {
//            setTitle("Registrasi Gagal")
//            setMessage(message)
//            setPositiveButton("OK", null)
//            show()
//        }
//    }

    private fun setupTogglePassword(){
        binding.ivToggleRegPassword.setOnClickListener{
            isPasswordVisible = !isPasswordVisible
            if(isPasswordVisible) {
                binding.passwordInput.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.ivToggleRegPassword.setImageResource(R.drawable.ic_visible)
            } else {
                binding.passwordInput.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.ivToggleRegPassword.setImageResource(R.drawable.ic_visibility_off)
            }
            binding.passwordInput.setSelection(binding.passwordInput.text?.length ?: 0)
        }

    }
}
