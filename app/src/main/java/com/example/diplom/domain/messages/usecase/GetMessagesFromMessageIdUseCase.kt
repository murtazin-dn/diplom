package com.example.diplom.domain.messages.usecase

import com.example.diplom.data.network.messages.model.response.MessageResponse
import com.example.diplom.data.network.util.Response
import com.example.diplom.domain.messages.repository.MessagesRepository
import kotlinx.coroutines.flow.Flow

class GetMessagesFromMessageIdUseCase(
    private val messagesRepository: MessagesRepository
) {
    suspend fun execute(chatId: Long, messageId: Long): Flow<Response<List<MessageResponse>>> =
        messagesRepository.getMessagesFromMessageId(chatId, messageId)
}