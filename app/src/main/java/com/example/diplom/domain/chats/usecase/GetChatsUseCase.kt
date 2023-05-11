package com.example.diplom.domain.chats.usecase

import com.example.diplom.data.network.chats.model.response.ChatPreview
import com.example.diplom.data.network.util.Response
import com.example.diplom.domain.chats.repository.ChatRepository
import kotlinx.coroutines.flow.Flow

class GetChatsUseCase(
    private val chatRepository: ChatRepository
) {
    suspend fun execute(): Flow<Response<List<ChatPreview>>> =
        chatRepository.getMyChats()
}