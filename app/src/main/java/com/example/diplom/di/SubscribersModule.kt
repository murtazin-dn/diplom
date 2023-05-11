package com.example.diplom.di

import com.example.diplom.data.network.auth.repository.AuthRepositoryImpl
import com.example.diplom.data.network.subscribers.repository.SubscribersRepositoryImpl
import com.example.diplom.domain.auth.repository.AuthRepository
import com.example.diplom.domain.auth.usecase.SignUpUseCase
import com.example.diplom.domain.subscribers.repository.SubscribersRepository
import com.example.diplom.domain.subscribers.usecase.SubscribeUseCase
import com.example.diplom.domain.subscribers.usecase.UnsubscribeUseCase
import com.example.diplom.util.TokenService
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val subscribersModule = module{
    single<SubscribersRepository> { SubscribersRepositoryImpl( service = get(), tokenService = get()) }
    single { SubscribeUseCase(subscribersRepository = get()) }
    single { UnsubscribeUseCase(subscribersRepository = get()) }
}