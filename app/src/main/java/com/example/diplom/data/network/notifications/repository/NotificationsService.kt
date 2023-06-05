package com.example.diplom.data.network.notifications.repository

import com.example.diplom.data.network.notifications.model.FCMTokenRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface NotificationsService {

    @POST("notifications/subscribe")
    suspend fun subscribe(@Header("Authorization") token: String, @Body fcmToken: FCMTokenRequest):
            Response<Unit>
    @POST("notifications/unsubscribe")
    suspend fun unsubscribe(@Header("Authorization") token: String, @Body fcmToken: FCMTokenRequest):
            Response<Unit>
}