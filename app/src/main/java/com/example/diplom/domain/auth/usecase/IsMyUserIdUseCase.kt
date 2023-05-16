package com.example.diplom.domain.auth.usecase

import android.util.Log
import com.example.diplom.util.TokenService

class IsMyUserIdUseCase(
    private val tokenService: TokenService
) {
    fun isMyUserId(id: Long): Boolean {
        val userId = tokenService.getUserId()
        Log.e("usrId", userId.toString())
        Log.e("Id", id.toString())
        return id == userId
    }
}