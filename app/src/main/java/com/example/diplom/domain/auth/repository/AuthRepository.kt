package com.example.diplom.domain.auth.repository

import com.example.diplom.data.network.auth.model.request.SignInRequest
import com.example.diplom.data.network.auth.model.request.SignUpRequest
import com.example.diplom.data.network.auth.model.response.AuthResponse
import com.example.diplom.data.network.util.Response
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun signUp(signUpRequest: SignUpRequest): Flow<Response<AuthResponse>>
    suspend fun signIn(signInRequest: SignInRequest): Flow<Response<AuthResponse>>
    suspend fun isTakenEmail(email: String): Flow<Response<Boolean>>
}