package com.example.diplom.domain.notifications.usecase

import android.util.Log
import com.example.diplom.data.network.util.Response
import com.example.diplom.domain.notifications.repository.NotificationsRepository
import kotlinx.coroutines.flow.Flow

class UnsubscribeNotificationsUseCase(
    private val notificationsRepository: NotificationsRepository
) {
    suspend fun execute(token: String): Flow<Response<Unit>> {
        return notificationsRepository.subscribe(token)
    }
}