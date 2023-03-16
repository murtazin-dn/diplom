package com.example.diplom.domain.repository

import com.example.diplom.data.network.SimpleResponse
import com.example.diplom.data.network.model.request.SignUpRequest
import com.example.diplom.data.network.model.response.AuthResponse

interface AuthRepository {
    suspend fun signUp(signUpRequest: SignUpRequest): SimpleResponse<AuthResponse>
}