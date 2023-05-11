package com.example.diplom.di

import com.example.diplom.presentation.post.adapter.ViewHoldersManager
import com.example.diplom.presentation.post.adapter.ViewHoldersManagerImpl
import org.koin.dsl.module

val uiModule = module {
    single<ViewHoldersManager> { ViewHoldersManagerImpl() }
}