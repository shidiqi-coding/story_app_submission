    package com.dicoding.storyapp


    import androidx.lifecycle.liveData
    import com.dicoding.storyapp.data.ResultState
    import com.dicoding.storyapp.data.pref.UserModel
    import com.dicoding.storyapp.data.pref.UserPreference
    import com.dicoding.storyapp.data.response.ListStoryItem
    import com.dicoding.storyapp.data.response.LoginResponse
    import com.dicoding.storyapp.data.response.UploadResponse
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


        fun login(email: String, password: String) = liveData {
            emit(ResultState.Loading)
            try {
                val response = apiService.login(email, password)
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





        fun uploadImage(imageFile: File , description: String) = liveData {
            emit(ResultState.Loading)
            val requestBody = description.toRequestBody("text/plain".toMediaType())
            val user = userPreference.getSession().first()
            val token = "Bearer ${user.token}"
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo" ,
                imageFile.name ,
                requestImageFile
            )
            try {
                val successResponse = apiService.uploadStory(token , multipartBody , requestBody)
                emit(ResultState.Success(successResponse))
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody , UploadResponse::class.java)
                emit(ResultState.Error(errorResponse.message ?: "Terjadi kesalahan saat upload"))
            }

        }

        suspend fun getStories(): List<ListStoryItem> {
            val user = userPreference.getSession().first()
            val token = "Bearer ${user.token}"
            val response = apiService.getStories(token)
            return response.listStory?.filterNotNull() ?: emptyList()
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