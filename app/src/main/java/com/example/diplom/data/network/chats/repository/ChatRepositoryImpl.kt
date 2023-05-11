package com.example.diplom.data.network.chats.repository

import com.example.diplom.data.network.chats.model.response.ChatPreview
import com.example.diplom.data.network.chats.model.response.ChatResponse
import com.example.diplom.data.network.util.Response
import com.example.diplom.domain.chats.repository.ChatRepository
import com.example.diplom.util.TokenService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ChatRepositoryImpl(
    private val tokenService: TokenService,
    private val chatService: ChatService
) : ChatRepository {
    override suspend fun getMyChats(): Flow<Response<List<ChatPreview>>> {
        return flow{
            val token = tokenService.getToken()
            val response = chatService.getMyChats("Bearer $token")
            if(response.isSuccessful){
                val body = response.body()!!
                emit(Response.Success(body))
            }else{
                emit(Response.Error(response.code(), response.errorBody().toString()))
            }
        }
    }

    override suspend fun getChatByChatId(chatId: Long): Flow<Response<ChatResponse>> {
        return flow{
            val token = tokenService.getToken()
            val response = chatService.getChatByChatId("Bearer $token", chatId)
            if(response.isSuccessful){
                val body = response.body()!!
                emit(Response.Success(body))
            }else{
                emit(Response.Error(response.code(), response.errorBody().toString()))
            }
        }
    }

    override suspend fun getChatByUserId(userId: Long): Flow<Response<ChatResponse>> {
        return flow{
            val token = tokenService.getToken()
            val response = chatService.getChatByUserId("Bearer $token", userId)
            if(response.isSuccessful){
                val body = response.body()!!
                emit(Response.Success(body))
            }else{
                emit(Response.Error(response.code(), response.errorBody().toString()))
            }
        }
    }
}