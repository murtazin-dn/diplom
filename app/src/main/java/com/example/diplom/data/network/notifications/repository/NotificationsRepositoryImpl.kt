package com.example.diplom.data.network.notifications.repository

import com.example.diplom.data.network.notifications.model.FCMTokenRequest
import com.example.diplom.data.network.util.Response
import com.example.diplom.domain.notifications.repository.NotificationsRepository
import com.example.diplom.util.TokenService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class NotificationsRepositoryImpl(
    private val notificationsService: NotificationsService,
    private val tokenService: TokenService
) : NotificationsRepository {
    override suspend fun subscribe(token: String): Flow<Response<Unit>> {
        return flow{
            val userToken = tokenService.getToken()
            val response = notificationsService.subscribe("Bearer $userToken", FCMTokenRequest(token))
            if(response.isSuccessful){
                emit(Response.Success(Unit))
            }else{
                emit(Response.Error(response.code(), response.errorBody().toString()))
            }
        }
    }
    override suspend fun unsubscribe(token: String): Flow<Response<Unit>> {
        return flow{
            val userToken = tokenService.getToken()
            val response = notificationsService.unsubscribe("Bearer $userToken", FCMTokenRequest(token))
            if(response.isSuccessful){
                emit(Response.Success(Unit))
            }else{
                emit(Response.Error(response.code(), response.errorBody().toString()))
            }
        }
    }
}