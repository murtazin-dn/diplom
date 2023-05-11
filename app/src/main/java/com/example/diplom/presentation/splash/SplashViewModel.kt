package com.example.diplom.presentation.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.diplom.util.TokenService

class SplashViewModel(
    private val tokenService: TokenService
) : ViewModel() {

    private val _isAuthorizedLiveData = MutableLiveData<Boolean>()
    val isAuthorizedLiveData: LiveData<Boolean> get() = _isAuthorizedLiveData

    fun isAuthorized() {
        _isAuthorizedLiveData.value = tokenService.containsToken()
    }

}