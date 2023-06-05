package com.example.diplom.domain.chats.usecase

import com.example.diplom.domain.chats.repository.ChatRepository

class GetUnreadDialogsCountUseCase(
    private val chatRepository: ChatRepository
) {
    suspend fun execute() = chatRepository.getUnreadDialogsCount()
}