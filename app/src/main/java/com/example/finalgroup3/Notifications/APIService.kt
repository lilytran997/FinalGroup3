package com.example.finalgroup3.Notifications

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface APIService {

    @Headers(
        "Content-Type:application/json",
        "Authorization:key=AAAAlz09qbE:APA91bF5OzDWBG3Skm7q1aRVVrHtWKRg_BEaiPcVChXmkwd8YpXql53wMXD1LrWF9v0beYbJRS8bSpuQYZyDn59YPLKmV_EMMy6QOW-I4HyVoBYJhB6gV1B4fN2r6eNvhahKUgH2pAla"
    )
    @POST("fcm/send")
    fun sendNoti(@Body body: Sender): Call<MyRespose>
}