package com.example.diplom.di

import com.example.diplom.data.network.chats.repository.ChatRepositoryImpl
import com.example.diplom.data.network.messages.repository.WebSocketClient
import com.example.diplom.domain.chats.repository.ChatRepository
import com.example.diplom.domain.chats.usecase.GetChatByChatIdUseCase
import com.example.diplom.domain.chats.usecase.GetChatByUserIdUseCase
import com.example.diplom.domain.chats.usecase.GetChatsUseCase
import org.koin.dsl.module

val chatModule = module {
    single<ChatRepository>{ChatRepositoryImpl(tokenService = get(), chatService = get())}
    single{GetChatsUseCase(chatRepository = get())}
    single{GetChatByChatIdUseCase(chatRepository = get())}
    single{ GetChatByUserIdUseCase(chatRepository = get()) }
    factory { WebSocketClient(tokenService = get()) }
}