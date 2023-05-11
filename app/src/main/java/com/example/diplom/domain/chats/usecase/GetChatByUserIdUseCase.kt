package com.example.diplom.domain.chats.usecase

import com.example.diplom.domain.chats.repository.ChatRepository

class GetChatByUserIdUseCase(
    private val chatRepository: ChatRepository
) {
    suspend fun execute(userId: Long) = chatRepository.getChatByUserId(userId)
}