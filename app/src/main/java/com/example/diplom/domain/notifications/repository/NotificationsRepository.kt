package com.example.diplom.domain.notifications.repository

import com.example.diplom.data.network.util.Response
import kotlinx.coroutines.flow.Flow

interface NotificationsRepository {
    suspend fun subscribe(token: String): Flow<Response<Unit>>
    suspend fun unsubscribe(token: String): Flow<Response<Unit>>
}