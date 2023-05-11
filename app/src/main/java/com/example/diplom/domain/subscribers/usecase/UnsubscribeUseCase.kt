package com.example.diplom.domain.subscribers.usecase

import com.example.diplom.domain.subscribers.repository.SubscribersRepository

class UnsubscribeUseCase(
    private val subscribersRepository: SubscribersRepository
) {
    suspend fun execute(userId: Long) = subscribersRepository.unsubscribe(userId)
}