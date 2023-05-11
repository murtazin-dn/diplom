package com.example.diplom.presentation

import android.app.Application
import com.example.diplom.di.networkModule
import com.example.diplom.di.repositoryModule
import com.example.diplom.di.uiModule
import com.example.diplom.di.viewModelModule
import com.example.diplom.util.TokenService
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(listOf(repositoryModule, networkModule, viewModelModule, uiModule))
        }
    }

}