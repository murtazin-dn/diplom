package com.example.diplom.di

import com.example.diplom.presentation.signin.SignInViewModel
import com.example.diplom.presentation.signup.SignUpViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { SignUpViewModel(signUpUseCase =  get(), getCategoriesUseCase = get()) }
    viewModel { SignInViewModel(signInUseCase =  get()) }
}