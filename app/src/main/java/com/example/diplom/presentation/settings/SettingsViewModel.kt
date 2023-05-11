package com.example.diplom.presentation.settings

import androidx.lifecycle.ViewModel
import com.example.diplom.util.TokenService

class SettingsViewModel(
    private val tokenService: TokenService
) : ViewModel() {
    fun clearToken(){
        tokenService.clearToken()
    }
}