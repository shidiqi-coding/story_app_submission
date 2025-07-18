package com.dicoding.storyapp


import android.content.Context
import android.util.Log
import androidx.lifecycle.liveData
import com.dicoding.storyapp.data.ResultState
import com.dicoding.storyapp.data.pref.UserModel
import com.dicoding.storyapp.data.pref.UserPreference
import com.dicoding.storyapp.data.response.ListStoryItem
import com.dicoding.storyapp.data.response.UploadResponse
import com.dicoding.storyapp.data.retrofit.ApiConfig
import com.dicoding.storyapp.data.retrofit.ApiService
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import okhttp3.MultipartBody
import retrofit2.HttpException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class StoryRepository(
    private val apiService: ApiService ,
    private val userPreference: UserPreference
) {


    fun login(email: String , password: String) = liveData {
        emit(ResultState.Loading)
        try {
            val response = apiService.login(email , password)
            val error = response.error ?: true
            val loginResult = response.loginResult

            if (!error && loginResult != null) {
                emit(ResultState.Success(loginResult))
            } else {
                emit(ResultState.Error(response.message ?: "Login gagal"))
            }
        } catch (e: HttpException) {
            val errorMsg = e.response()?.errorBody()?.string()
            emit(ResultState.Error(errorMsg ?: "Terjadi kesalahan jaringan"))
        }
    }


    fun register(name: String , email: String , password: String) = liveData {
        emit(ResultState.Loading)
        try {
            val response = apiService.register(name , email , password)
            //val error = response.error ?: true
            if (response.error == false) {
                emit(ResultState.Success(response.message ?: "Registrasi berhasil"))
            } else {
                emit(ResultState.Error(response.message ?: "Registrasi gagal"))
            }
        } catch (e: HttpException) {
            val errorMsg = e.response()?.errorBody()?.string()
            emit(ResultState.Error(errorMsg ?: "Terjadi kesalahan jaringan"))
        }
    }


    fun uploadImage(imageFile: File, description: String, context: Context) = liveData {
        emit(ResultState.Loading)

        val user = userPreference.getSession().first()
        val token = "Bearer ${user.token}"

        // Logging
        Log.d("UPLOAD", "Token: $token")
        Log.d("UPLOAD", "Desc: $description")
        Log.d("UPLOAD", "File exists: ${imageFile.exists()} size=${imageFile.length()}")

        // Request body
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo", imageFile.name, requestImageFile
        )

        val apiService = ApiConfig.getApiService(token)

        try {
            val response = apiService.uploadStory(
                token,
                multipartBody,
                requestBody
            )
            emit(ResultState.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("UPLOAD_ERROR", "HTTP Exception: ${e.code()}", e)
            Log.e("UPLOAD_ERROR", "Error body: $errorBody")

            val errorResponse = try {
                Gson().fromJson(errorBody, UploadResponse::class.java)
            } catch (ex: Exception) {
                UploadResponse(error = true, message = context.getString(R.string.error_parse_message))
            }

            emit(ResultState.Error(errorResponse.message ?: "Terjadi kesalahan saat upload"))
        } catch (e: Exception) {
            Log.e("UPLOAD_ERROR", "Exception: ${e.message}", e)
            emit(ResultState.Error(e.message ?: "Terjadi kesalahan tidak dikenal"))
        }
    }
    suspend fun getStories(): List<ListStoryItem> {
        val user = userPreference.getSession().first()
        val token = "Bearer ${user.token}"
        Log.d("DEBUG_TOKEN" , token)

        return try {
            val response = apiService.getStories(token)
            response.listStory?.filterNotNull() ?: emptyList()
        } catch (e: HttpException) {
            throw Exception("Terjadi kesalahan server: ${e.code()} ${e.message()}")
        } catch (_: java.io.IOException) {
            throw Exception("Koneksi ke server gagal. Periksa jaringan Anda.")
        }
    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var INSTANCE: StoryRepository? = null

        fun getInstance(
            apiService: ApiService ,
            userPreference: UserPreference
        ): StoryRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = StoryRepository(apiService , userPreference)
                INSTANCE = instance
                instance
            }
        }

    }
}