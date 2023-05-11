package com.example.diplom.domain.chats.usecase

import com.example.diplom.data.network.chats.model.response.ChatResponse
import com.example.diplom.data.network.util.Response
import com.example.diplom.domain.chats.repository.ChatRepository
import kotlinx.coroutines.flow.Flow

class GetChatByChatIdUseCase(
    private val chatRepository: ChatRepository
) {
    suspend fun execute(chatId: Long): Flow<Response<ChatResponse>> =
        chatRepository.getChatByChatId(chatId)
}