package com.example.dogdispenser.api.service

import com.facebook.stetho.okhttp3.StethoInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object APIService {

    private var INSTANCE: DogDispenserService? = null

    val instance: DogDispenserService?

    get() {
        if (INSTANCE == null) {
            val client = OkHttpClient.Builder()
                .addNetworkInterceptor(StethoInterceptor())
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("http://10.0.2.2:1880")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            INSTANCE = retrofit.create(DogDispenserService::class.java)
        }

        return INSTANCE
    }
}