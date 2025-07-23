package com.dicoding.storyapp.view.setting

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.storyapp.R
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.ViewModelFactory
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.dicoding.storyapp.databinding.ActivitySettingBinding

class SettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding
    private lateinit var viewModel: SettingViewModel
    private var currentThemeChoice: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(application)
        )[SettingViewModel::class.java]



        viewModel.getThemeSettings().observe(this) { theme ->
            currentThemeChoice = theme
            binding.tvAppearanceSubtitle.text = when (theme) {
                0 -> getString(R.string.device_theme)
                1 -> getString(R.string.light_theme)
                2 -> getString(R.string.dark_theme)
                else -> getString(R.string.device_theme)
            }
        }

        binding.appearanceLayout.setOnClickListener {
            showAppearanceDialog()
        }
    }

    private fun showAppearanceDialog() {
        val options = arrayOf(getString(R.string.device_theme),getString(R.string.light_theme), getString(R.string.dark_theme))
//        val checkedItem = viewModel.themeChoice.value ?: 0

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
            cancelButton.setTextColor(
                ContextCompat.getColor(this, R.color.button_color)
            )
        }

        dialog.show()
    }
}
