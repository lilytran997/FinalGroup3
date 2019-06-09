package com.example.finalgroup3.Notifications

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class client {

    private var retrofit: Retrofit? = null
    fun getClient(url: String): Retrofit? {
        if (retrofit == null) {
            retrofit = Retrofit.Builder().baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        }
        return this.retrofit
    }
}