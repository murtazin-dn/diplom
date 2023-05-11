package com.example.diplom.data.network.messages.repository

import com.example.diplom.data.network.messages.model.response.MessageResponse
import com.example.diplom.data.network.util.Response
import com.example.diplom.domain.messages.repository.MessagesRepository
import com.example.diplom.util.TokenService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MessagesRepositoryImpl(
    private val messagesService: MessagesService,
    private val tokenService: TokenService
) : MessagesRepository {
    override suspend fun getMessages(chatId: Long): Flow<Response<List<MessageResponse>>> {
        return flow {
            val token = tokenService.getToken()
            val response = messagesService.getMessagesByChat("Bearer $token", chatId)
            if(response.isSuccessful){
                val body = response.body()!!
                emit(Response.Success(body))
            }else{
                emit(Response.Error(response.code(), response.errorBody().toString()))
            }
        }
    }
}