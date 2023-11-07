package com.example.justalk_2.notifications.network

import com.example.justalk_2.notifications.Constants.Companion.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {
    companion object{
        private val retrofit by lazy{
            Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()
        }

        val api by lazy {
            retrofit.create(NotificationApi::class.java)
        }
    }
}