package com.example.diplom.domain.auth.usecase

import com.example.diplom.data.network.util.Response
import com.example.diplom.domain.auth.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class IsTakenEmailUseCase(
    private val authRepository: AuthRepository
) {
    suspend fun execute(email: String): Flow<Response<Boolean>>{
        return authRepository.isTakenEmail(email)
    }
}