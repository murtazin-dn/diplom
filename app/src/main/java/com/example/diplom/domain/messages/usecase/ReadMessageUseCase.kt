package com.example.diplom.domain.messages.usecase

import com.example.diplom.domain.messages.repository.MessagesRepository

class ReadMessageUseCase(
    private val messagesRepository: MessagesRepository
) {
    suspend fun execute(chatId: Long, messageId: Long) = messagesRepository.readMessage(chatId, messageId)
}