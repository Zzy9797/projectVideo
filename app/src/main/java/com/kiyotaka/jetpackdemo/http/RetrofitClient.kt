package com.kiyotaka.jetpackdemo.http

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private val BASE_URL = "https://api.gdbrainview.com/"

    val reqApi by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(ResponseConverterFactory.create())
            .build()
        return@lazy retrofit.create(APIs::class.java)
    }
}