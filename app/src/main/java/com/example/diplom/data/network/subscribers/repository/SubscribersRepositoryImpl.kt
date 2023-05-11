package com.example.diplom.data.network.subscribers.repository

import com.example.diplom.data.network.util.Response
import com.example.diplom.domain.subscribers.repository.SubscribersRepository
import com.example.diplom.util.TokenService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SubscribersRepositoryImpl(
    private val tokenService: TokenService,
    private val service: SubscribersService
): SubscribersRepository {
    override suspend fun subscribe(userId: Long): Flow<Response<Unit>> {
        return flow{
            val token = tokenService.getToken()
            val response = service.subscribe("Bearer $token", userId)
            if(response.isSuccessful){
                emit(Response.Success(Unit))
            }else{
                emit(Response.Error(response.code(), response.errorBody().toString()))
            }
        }
    }

    override suspend fun unsubscribe(userId: Long): Flow<Response<Unit>> {
        return flow{
            val token = tokenService.getToken()
            val response = service.unsubscribe("Bearer $token", userId)
            if(response.isSuccessful){
                emit(Response.Success(Unit))
            }else{
                emit(Response.Error(response.code(), response.errorBody().toString()))
            }
        }
    }
}