package com.dicoding.storyapp.data.retrofit

import com.dicoding.storyapp.data.response.RegisterResponse
import com.dicoding.storyapp.data.response.StoryResponse
//import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Field
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String ,
        @Field("email") email: String ,
        @Field("password") password: String
    ): RegisterResponse


    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") token: String ,
    ): StoryResponse
}