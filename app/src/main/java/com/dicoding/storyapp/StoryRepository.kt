package com.dicoding.storyapp



import android.annotation.SuppressLint
import android.content.Context
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
import org.json.JSONObject
import java.io.IOException

class StoryRepository(
    private val apiService: ApiService ,
    private val userPreference: UserPreference,
    private val context: Context
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
                emit(ResultState.Error(response.message ?: context.getString(R.string.login_failed_title)))
            }
        } catch (e: HttpException) {
            val errorMsg = e.response()?.errorBody()?.string()
            emit(ResultState.Error(errorMsg ?: context.getString(R.string.error_network)))

        }
    }


    fun register(name: String, email: String, password: String) = liveData {
        emit(ResultState.Loading)
        try {
            val response = apiService.register(name, email, password)
            if (response.error == false) {
                val successMessage = String.format(
                    context.getString(R.string.register_success_message),
                    email
                )
                emit(ResultState.Success(Pair(name, successMessage)))
            } else {
                emit(ResultState.Error(response.message ?: context.getString(R.string.register_failed)))
            }
        } catch (e: HttpException) {
            val errorMsg = try {
                val errorBody = e.response()?.errorBody()?.string()
                JSONObject(errorBody ?: "").getString("message")
            } catch (_: Exception) {
                context.getString(R.string.error_network)
            }
            emit(ResultState.Error(errorMsg))
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
        Log.d("DEBUG_TOKEN", token)

        return try {
            val response = apiService.getStories(token)
            response.listStory?.filterNotNull() ?: emptyList()
        } catch (e: HttpException) {
            val code = e.code()
            val message = e.message()
            Log.e("GET_STORIES_ERROR", "HTTP exception: $code $message")
            throw Exception(context.getString(R.string.error_server, code, message))
        } catch (e: IOException) {
            Log.e("GET_STORIES_ERROR", "Network error: ${e.message}")
            throw Exception(context.getString(R.string.error_connection))
        }
    }



    @SuppressLint("StringFormatInvalid")
    suspend fun getStoryDetail(storyId: String): Story {
        val user = userPreference.getSession().first()
        val token = "Bearer ${user.token}"

        return try {
            val response = apiService.getStoriesDetail(token, storyId)
            if (response.error == false && response.story != null) {
                response.story
            } else {
                throw Exception(context.getString(R.string.error_detail_connection, response.message))

            }
        } catch (e: HttpException) {
            throw Exception("HTTP Error: ${e.code()} ${e.message()}")
        } catch (e: Exception) {
            throw Exception(context.getString(R.string.error_message, e.message))
        }
    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): LiveData<UserModel> {
        return userPreference.getSession().asLiveData()
    }


    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: StoryRepository? = null

        fun getInstance(
            apiService: ApiService ,
            userPreference: UserPreference,
            context : Context
        ): StoryRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = StoryRepository(apiService , userPreference, context)
                INSTANCE = instance
                instance
            }
        }

    }
}