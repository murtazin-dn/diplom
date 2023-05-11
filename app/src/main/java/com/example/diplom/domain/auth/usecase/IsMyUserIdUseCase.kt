package com.example.diplom.domain.auth.usecase

import com.example.diplom.util.TokenService

class IsMyUserIdUseCase(
    private val tokenService: TokenService
) {
    fun isMyUserId(id: Long): Boolean = id == tokenService.getUserId()
}