package com.example.diplom.di

import com.example.diplom.data.network.notifications.repository.NotificationsRepositoryImpl
import com.example.diplom.domain.notifications.repository.NotificationsRepository
import com.example.diplom.domain.notifications.usecase.SubscribeNotificationsUseCase
import org.koin.dsl.module

val notificationsModule = module {
    single<NotificationsRepository> {
        NotificationsRepositoryImpl(notificationsService = get(), tokenService = get()) }
    single { SubscribeNotificationsUseCase(notificationsRepository = get()) }
}