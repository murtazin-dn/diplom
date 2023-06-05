package com.example.diplom.domain.messages.repository

import com.example.diplom.data.network.chats.model.response.ChatResponse
import com.example.diplom.data.network.messages.model.response.MessageResponse
import com.example.diplom.data.network.util.Response
import kotlinx.coroutines.flow.Flow

interface MessagesRepository {
    suspend fun getMessages(chatId: Long): Flow<Response<List<MessageResponse>>>
    suspend fun getMessagesFromMessageId(chatId: Long, messageId: Long): Flow<Response<List<MessageResponse>>>
    suspend fun readMessage(chatId: Long, messageId: Long): Flow<Response<Unit>>
}