package com.example.diplom.domain.chats.repository

import com.example.diplom.data.network.chats.model.response.ChatPreview
import com.example.diplom.data.network.chats.model.response.ChatResponse
import com.example.diplom.data.network.util.Response
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun getMyChats(): Flow<Response<List<ChatPreview>>>
    suspend fun getChatByChatId(chatId: Long): Flow<Response<ChatResponse>>
    suspend fun getChatByUserId(userId: Long): Flow<Response<ChatResponse>>
}