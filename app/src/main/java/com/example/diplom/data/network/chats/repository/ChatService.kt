package com.example.diplom.data.network.chats.repository

import com.example.diplom.data.network.chats.model.response.ChatPreview
import com.example.diplom.data.network.chats.model.response.ChatResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface ChatService {
    @GET("chats")
    suspend fun getMyChats(@Header("Authorization") token: String): Response<List<ChatPreview>>

    @GET("chats/chat/{chatId}")
    suspend fun getChatByChatId(
        @Header("Authorization") token: String,
        @Path("chatId") chatId: Long)
    : Response<ChatResponse>

    @GET("chats/user/{userId}")
    suspend fun getChatByUserId(
        @Header("Authorization") token: String,
        @Path("userId") userId: Long)
    : Response<ChatResponse>

    @GET("chats/unreadcount")
    suspend fun getCountUnreadDialogs(
        @Header("Authorization") token: String)
    : Response<Long>
}