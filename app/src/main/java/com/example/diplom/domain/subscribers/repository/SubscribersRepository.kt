package com.example.diplom.domain.subscribers.repository

import com.example.diplom.data.network.util.Response
import kotlinx.coroutines.flow.Flow

interface SubscribersRepository {
    suspend fun subscribe(userId: Long): Flow<Response<Unit>>
    suspend fun unsubscribe(userId: Long): Flow<Response<Unit>>
}