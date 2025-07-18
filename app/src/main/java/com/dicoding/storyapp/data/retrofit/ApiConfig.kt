package com.dicoding.storyapp.data.retrofit

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiConfig {

    companion object {
        fun getApiService(token: String): ApiService {
            val loggingInterceptor = HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY)

//            val authInterceptor = Interceptor { chain ->
//                val req = chain.request()
//                val newReq = req.newBuilder()
//                    .addHeader("Authorization", "Bearer $token")
//                    .build()
//                chain.proceed(newReq)
//            }

            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
               // .addInterceptor(authInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .callTimeout(60, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl("https://story-api.dicoding.dev/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
                .create(ApiService::class.java)
        }
    }
}
