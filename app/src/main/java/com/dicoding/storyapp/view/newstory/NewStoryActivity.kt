package com.dicoding.storyapp.view.newstory

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.R
import com.dicoding.storyapp.ViewModelFactory
import com.dicoding.storyapp.data.ResultState
import com.dicoding.storyapp.data.pref.UserModel
import com.dicoding.storyapp.databinding.ActivityNewStoryBinding
import com.dicoding.storyapp.getImageUri
import com.dicoding.storyapp.reduceFileImage
import com.dicoding.storyapp.uriToFile
import com.dicoding.storyapp.view.main.MainActivity
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MultipartBody

class NewStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewStoryBinding
    private lateinit var viewModel: NewStoryViewModel
    private var currentImageUri: Uri? = null
    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.cameraButton.setOnClickListener { startCamera() }
        binding.uploadButton.setOnClickListener { uploadImage() }
    }

    private fun setupViewModel() {
        val factory = ViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[NewStoryViewModel::class.java]

        viewModel.getSession().observe(this) { user: UserModel ->
            token = user.token
        }
    }

    private fun uploadImage() {
        val userToken = token
        if (userToken.isNullOrEmpty()) {
            showToast(getString(R.string.empty_image_warning))
            return
        }

        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            Log.d("Image File", "showImage: ${imageFile.path}")
            val description = binding.descriptionEditText.text.toString()

            if (description.isEmpty()) {
                showToast(getString(R.string.empty_description_warning))
                return
            }

            showLoading(true)

            val requestImage = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartImage = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImage
            )
            val descRequestBody = description.toRequestBody("text/plain".toMediaType())

            viewModel.uploadStory(userToken, multipartImage, descRequestBody).observe(this) { result ->
                when (result) {
                    is ResultState.Loading -> {
                        binding.loadingOverlay.visibility = View.VISIBLE

                    }
                    is ResultState.Success -> {
                        showToast(getString(R.string.upload_success))
                        binding.loadingOverlay.visibility = View.GONE
                        Toast.makeText(this, "Story uploaded successfully", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }
                    is ResultState.Error -> {
                        showToast(result.error)
                        showLoading(false)
                        Toast.makeText(this, "Upload failed: ${result.error}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        } ?: showToast(getString(R.string.empty_image_warning))
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressLoadingIndicator.visibility = if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
    }

    private fun startGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImage = result.data?.data
            selectedImage?.let {
                currentImageUri = it
                binding.storyImageView.setImageURI(it)
            }
        }
    }

    private fun startCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
        } else {
            cameraLauncher.launch(null)
        }
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let {
            val uri = getImageUri(this, it)
            currentImageUri = uri
            binding.storyImageView.setImageBitmap(it)
        }
    }

//    private fun goBackWithResult() {
//        setResult(RESULT_OK)
//        finish()
//    }

    companion object {
        const val REQUEST_CAMERA_PERMISSION = 100
    }
}
