package com.example.diplom.data.network.subscribers.repository

import com.example.diplom.data.network.personinfo.model.response.MyProfileResponse
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface SubscribersService {

    @POST("subscribers/{userId}")
    suspend fun subscribe(
        @Header("Authorization") token: String,
        @Path("userId") userId: Long
    ): Response<Unit>

    @DELETE("subscribers/{userId}")
    suspend fun unsubscribe(
        @Header("Authorization") token: String,
        @Path("userId") userId: Long
    ): Response<Unit>
}