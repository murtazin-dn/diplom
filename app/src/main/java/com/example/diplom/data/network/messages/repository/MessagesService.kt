package com.example.diplom.data.network.messages.repository

import com.example.diplom.data.network.messages.model.response.MessageResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface MessagesService {
    @GET("chat/{chatId}/messages")
    suspend fun getMessagesByChat(
        @Header("Authorization") token: String,
        @Path("chatId") chatId: Long)
    : Response<List<MessageResponse>>
}