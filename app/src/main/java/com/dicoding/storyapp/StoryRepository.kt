package com.dicoding.storyapp



import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.dicoding.storyapp.data.ResultState
import com.dicoding.storyapp.data.pref.UserModel
import com.dicoding.storyapp.data.pref.UserPreference
import com.dicoding.storyapp.data.response.ListStoryItem
import com.dicoding.storyapp.data.response.UploadResponse
import com.dicoding.storyapp.data.response.Story
import com.dicoding.storyapp.data.retrofit.ApiService
import kotlinx.coroutines.flow.first
import okhttp3.MultipartBody
import retrofit2.HttpException
import okhttp3.RequestBody

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


    fun uploadStory(
        token: String ,
        image: MultipartBody.Part ,
        description: RequestBody ,
        lat: RequestBody? = null ,
        lon: RequestBody? = null
    ): LiveData<ResultState<UploadResponse>> = liveData {
        emit(ResultState.Loading)
        try {
            val response = apiService.uploadStory("Bearer $token" , image , description , lat , lon)
            val body = response.body()
            if (response.isSuccessful && body != null) {
                emit(ResultState.Success(body))
            } else {
                emit(ResultState.Error(response.message()))
            }
        } catch (e: HttpException) {
            val errorMessage = e.response()?.errorBody()?.string() ?: "Unknown error"
            emit(ResultState.Error(errorMessage))
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

    suspend fun getStoryDetail(storyId: String): Story {
        val user = userPreference.getSession().first()
        val token = "Bearer ${user.token}"

        return try {
            val response = apiService.getStoriesDetail(token, storyId)
            if (response.error == false && response.story != null) {
                response.story
            } else {
                throw Exception("Gagal mengambil detail cerita: ${response.message}")
            }
        } catch (e: HttpException) {
            throw Exception("HTTP Error: ${e.code()} ${e.message()}")
        } catch (e: Exception) {
            throw Exception("Terjadi kesalahan: ${e.message}")
        }
    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): LiveData<UserModel> {
        return userPreference.getSession().asLiveData()
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