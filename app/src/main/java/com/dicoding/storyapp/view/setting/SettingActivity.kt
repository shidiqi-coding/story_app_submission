package com.dicoding.storyapp.view.setting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.R
import com.dicoding.storyapp.view.main.MainActivity
import com.dicoding.storyapp.ViewModelFactory
import com.dicoding.storyapp.databinding.ActivitySettingBinding
import com.dicoding.storyapp.view.helper.LocaleHelper

class SettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding
    private lateinit var viewModel: SettingViewModel
    private var currentThemeChoice: Int = 0


    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase!!))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(application)
        )[SettingViewModel::class.java]


        viewModel.themeChoice.observe(this) { theme ->
            currentThemeChoice = theme
            binding.tvAppearanceSubtitle.text = when (theme) {
                0 -> getString(R.string.device_theme)
                1 -> getString(R.string.light_theme)
                2 -> getString(R.string.dark_theme)
                else -> getString(R.string.device_theme)
            }
        }


        viewModel.languageSetting.observe(this) { lang ->
            binding.tvLanguageSubtitle.text = when (lang) {
                "en" -> getString(R.string.en_language)
                "id" -> getString(R.string.id_language)
                else -> getString(R.string.language_system)
            }
        }


        binding.appearanceLayout.setOnClickListener {
            showAppearanceDialog()
        }


        binding.localizationLayout.setOnClickListener {
            showLanguageDialog()
        }
    }

    private fun showAppearanceDialog() {
        val options = arrayOf(
            getString(R.string.device_theme),
            getString(R.string.light_theme),
            getString(R.string.dark_theme)
        )

        val dialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.choose_theme))
            .setSingleChoiceItems(options, currentThemeChoice) { dialog, which ->
                viewModel.setThemeChoice(which)
                dialog.dismiss()
                recreate()
            }
            .setNegativeButton(getString(R.string.cancel_button), null)
            .create()

        dialog.setOnShowListener {
            val cancelButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            cancelButton.setTextColor(ContextCompat.getColor(this, R.color.button_color))
        }

        dialog.show()
    }

    private fun showLanguageDialog() {
        val options = arrayOf(
            getString(R.string.language_system),
            getString(R.string.en_language),
            getString(R.string.id_language)
        )

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.language))
            .setItems(options) { _, which ->
                val lang = when (which) {
                    1 -> "en"
                    2 -> "id"
                    else -> null
                }

                viewModel.saveLanguageSetting(lang)
                LocaleHelper.saveLanguage(this, lang)


                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
            .show()
    }
}
