package com.dicoding.storyapp.view.newstory

import android.content.pm.PackageManager
import android.net.Uri
import android.Manifest
import android.os.Bundle
import com.dicoding.storyapp.R
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.dicoding.storyapp.ViewModelFactory
import com.dicoding.storyapp.data.ResultState
import com.dicoding.storyapp.reduceFileImage
import com.dicoding.storyapp.databinding.ActivityNewStoryBinding
import com.dicoding.storyapp.uriToFile


class NewStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewStoryBinding

    private val viewModel : NewStoryViewModel by viewModels{
        ViewModelFactory.getInstance(this)
    }

    private var currentImageUri: Uri? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this , "Permintaan izin diterima" , Toast.LENGTH_LONG).show()

        } else {
            Toast.makeText(this , "Permintaan izin ditolak" , Toast.LENGTH_LONG).show()
        }
    }

    private fun allPermissionGranted() = ContextCompat.checkSelfPermission(
        this , REQUIRED_PERMISSION
    ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityNewStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (allPermissionGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.cameraButton.setOnClickListener { startCamera() }
        binding.uploadButton.setOnClickListener { uploadImage() }

    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker" , "No media selected")
        }
    }

    private fun startCamera() {
        Toast.makeText(this , "Fitur ini belum tersedia" , Toast.LENGTH_SHORT).show()
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI" , "showImage: $it")
            binding.storyImageView.setImageURI(it)
        }
    }

    private fun uploadImage() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri , this).reduceFileImage()
            Log.d("Image File" , "showImage: ${imageFile.path}")
            val description = "ini adalah deskripsi gambar"

            showLoading(true)

//            val requestBody = description.toRequestBody("text/plain".toMediaType())
//            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
//            val multipartBody = MultipartBody.Part.createFormData(
//                "photo" ,
//                imageFile.name
//                        requestImageFile
//            )
//            lifecycleScope.launch {
//                try {
//                    val apiService = ApiConfig.getApiService()
//                    val successResponse = apiService.uploadImage(multipartBody, requestBody)
//                    showToast(successResponse.message)
//                    showLoading(false)
//
//                } catch (e:HttpException) {
//                    val errorBody = e.response()?.errorBody()?.string()
//                    val errorResponse= Gson().fromJson(errorBody, FileUploadResponse:class.java)
//                    showToast(errorResponse.message)
//                    showLoading(false)
//
//                }
//            }
//

            viewModel.uploadImage(imageFile , description, this).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is ResultState.Loading -> {
                            showLoading(true)
                        }

                        is ResultState.Success -> {
                            showToast(result.data.message())
                            showLoading(false)
                        }

                        is ResultState.Error -> {
                            showToast(result.error)
                            showLoading(false)
                        }


                    }


                }
            }

        } ?: showToast(getString(R.string.empty_image_warning))
    }


    private fun showToast(message: String) {
        Toast.makeText(this , message , Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressLoadingIndicator.isVisible = isLoading

    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }

}
