package com.example.diplom.di

import com.example.diplom.presentation.ui.post.adapter.ViewHoldersManager
import com.example.diplom.presentation.ui.post.adapter.ViewHoldersManagerImpl
import org.koin.dsl.module

val uiModule = module {
    single<ViewHoldersManager> { ViewHoldersManagerImpl() }
}