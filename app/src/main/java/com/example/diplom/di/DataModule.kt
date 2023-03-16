package com.example.diplom.di

import com.example.diplom.data.network.repository.auth.AuthRepositoryImpl
import com.example.diplom.domain.repository.AuthRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<AuthRepository> {
        AuthRepositoryImpl( apiClient = get())
    }
}