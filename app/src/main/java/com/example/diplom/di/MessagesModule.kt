package com.example.diplom.di

import com.example.diplom.data.network.messages.repository.MessagesRepositoryImpl
import com.example.diplom.data.network.messages.repository.MessagesService
import com.example.diplom.domain.messages.repository.MessagesRepository
import com.example.diplom.domain.messages.usecase.GetMessagesFromMessageIdUseCase
import com.example.diplom.domain.messages.usecase.GetMessagesUseCase
import com.example.diplom.domain.messages.usecase.ReadMessageUseCase
import org.koin.dsl.module

val messagesModule = module {
    single<MessagesRepository> { MessagesRepositoryImpl(messagesService = get(), tokenService = get()) }
    single {GetMessagesUseCase(messagesRepository = get())}
    single {GetMessagesFromMessageIdUseCase(messagesRepository = get())}
    single {ReadMessageUseCase(messagesRepository = get())}
}