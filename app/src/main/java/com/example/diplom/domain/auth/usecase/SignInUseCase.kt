package com.example.diplom.domain.auth.usecase

import android.util.Log
import com.example.diplom.data.network.auth.model.request.SignInRequest
import com.example.diplom.data.network.auth.model.request.SignUpRequest
import com.example.diplom.data.network.auth.model.response.AuthResponse
import com.example.diplom.data.network.util.Response
import com.example.diplom.domain.auth.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class SignInUseCase(
    private val authRepository: AuthRepository
) {
    suspend fun execute(signInRequest: SignInRequest): Flow<Response<AuthResponse>> {
        return authRepository.signIn(signInRequest)
    }
}