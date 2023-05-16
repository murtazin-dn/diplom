package com.example.diplom.domain.notifications.usecase

import android.util.Log
import com.example.diplom.data.network.util.Response
import com.example.diplom.domain.notifications.repository.NotificationsRepository
import kotlinx.coroutines.flow.Flow

class SubscribeNotificationsUseCase(
    private val notificationsRepository: NotificationsRepository
) {
    suspend fun execute(token: String): Flow<Response<Unit>> {
        Log.e("subscribe notification","usecase")
        return notificationsRepository.subscribe(token)
    }
}