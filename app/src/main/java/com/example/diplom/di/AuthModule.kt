package com.example.diplom.di

import com.example.diplom.data.network.auth.repository.AuthRepositoryImpl
import com.example.diplom.domain.auth.repository.AuthRepository
import com.example.diplom.domain.auth.usecase.IsMyUserIdUseCase
import com.example.diplom.domain.auth.usecase.IsTakenEmailUseCase
import com.example.diplom.domain.auth.usecase.SignInUseCase
import com.example.diplom.domain.auth.usecase.SignUpUseCase
import com.example.diplom.util.TokenService
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val authModule = module{
    single<AuthRepository> { AuthRepositoryImpl( authService = get(), tokenService = get()) }
    single { TokenService(context = androidContext()) }
    single { SignUpUseCase(authRepository = get()) }
    single { SignInUseCase(authRepository = get()) }
    single { IsTakenEmailUseCase(authRepository = get()) }
    single { IsMyUserIdUseCase(tokenService = get()) }

}