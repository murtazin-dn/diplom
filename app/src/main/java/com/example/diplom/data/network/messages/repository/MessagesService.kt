package com.example.diplom.data.network.messages.repository

import com.example.diplom.data.network.messages.model.response.MessageResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface MessagesService {
    @GET("chat/{chatId}/messages")
    suspend fun getMessagesByChat(
        @Header("Authorization") token: String,
        @Path("chatId") chatId: Long)
    : Response<List<MessageResponse>>

    @GET("chat/{chatId}/messages")
    suspend fun getMessagesFromMessageId(
        @Header("Authorization") token: String,
        @Path("chatId") chatId: Long,
        @Query("messageId") messageId: Long
    )
    : Response<List<MessageResponse>>

    @GET("chat/{chatId}/read/{messageId}")
    suspend fun readMessage(
        @Header("Authorization") token: String,
        @Path("chatId") chatId: Long,
        @Path("messageId") messageId: Long)
    : Response<Unit>
}